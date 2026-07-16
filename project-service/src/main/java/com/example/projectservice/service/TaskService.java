package com.example.projectservice.service;

import com.example.projectservice.dto.UserTaskStatsDto;
import com.example.sharedlib.config.RabbitMqAutoConfiguration;
import com.example.sharedlib.dto.TaskDto;
import com.example.sharedlib.event.EventPublisher;
import com.example.sharedlib.event.ProjectEvent;
import com.example.sharedlib.exception.ResourceNotFoundException;
import com.example.projectservice.model.Task;
import com.example.sharedlib.enums.TaskStatus;
import com.example.projectservice.model.ProjectMember;
import com.example.projectservice.repository.ProjectMemberRepository;
import com.example.projectservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectService projectService;
    private final ProjectMemberRepository projectMemberRepository;
    private final EventPublisher eventPublisher;
    private final RabbitTemplate rabbitTemplate;
    private final UserNameCacheService userNameCacheService;

    /**
     * Создать новую задачу
     */
    public TaskDto createTask(TaskDto taskDto) {
        log.info("Creating new task: {}", taskDto.getTitle());

        // Проверить существование проекта
        projectService.getProjectById(taskDto.getProjectId());

        // Validate assignee is a project member
        if (taskDto.getAssignedTo() != null) {
            validateProjectMembership(taskDto.getProjectId(), taskDto.getAssignedTo());
        }

        Task task = Task.builder()
                .id(UUID.randomUUID())
                .project(projectService.getProjectEntityById(taskDto.getProjectId()))
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .status(taskDto.getStatus() != null ? taskDto.getStatus() : TaskStatus.TO_DO)
                .assignedTo(taskDto.getAssignedTo())
                .dueDate(taskDto.getDueDate())
                .build();

        Task savedTask = taskRepository.save(task);

        // Notify assignee about the new task
        if (savedTask.getAssignedTo() != null) {
            rabbitTemplate.convertAndSend(
                    RabbitMqAutoConfiguration.NOTIFICATION_EXCHANGE,
                    RabbitMqAutoConfiguration.NOTIFICATION_SEND_KEY,
                    Map.of(
                            "userId", savedTask.getAssignedTo().toString(),
                            "title", "Новая задача",
                            "message", "Вам назначена задача: " + savedTask.getTitle(),
                            "type", "TASK_ASSIGNED"
                    )
            );
        }

        return mapToDto(savedTask);
    }

    /**
     * Получить задачу по ID
     */
    @Transactional(readOnly = true)
    public TaskDto getTaskById(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Task not found with id: {}", id);
                    return new ResourceNotFoundException("Task not found");
                });
        return enrichWithUserName(mapToDto(task));
    }

    /**
     * Получить все задачи проекта
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getProjectTasks(UUID projectId) {
        log.info("Getting tasks for project: {}", projectId);
        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(this::mapToDto)
                .map(this::enrichWithUserName)
                .collect(Collectors.toList());
    }

    /**
     * Получить задачи, назначенные пользователю
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getUserTasks(UUID userId) {
        return taskRepository.findByAssignedTo(userId)
                .stream()
                .map(this::mapToDto)
                .map(this::enrichWithUserName)
                .collect(Collectors.toList());
    }

    /**
     * Обновить задачу
     */
    public TaskDto updateTask(UUID id, TaskDto updateDto) {
        log.info("Updating task: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        UUID previousAssignee = task.getAssignedTo();

        if (updateDto.getTitle() != null) task.setTitle(updateDto.getTitle());
        if (updateDto.getDescription() != null) task.setDescription(updateDto.getDescription());
        if (updateDto.getAssignedTo() != null) {
            validateProjectMembership(task.getProject().getId(), updateDto.getAssignedTo());
            task.setAssignedTo(updateDto.getAssignedTo());
        }
        if (updateDto.getDueDate() != null) task.setDueDate(updateDto.getDueDate());

        task.setUpdatedAt(OffsetDateTime.now());

        Task updatedTask = taskRepository.save(task);

        // Notify new assignee if assignment changed
        if (updateDto.getAssignedTo() != null && !updateDto.getAssignedTo().equals(previousAssignee)) {
            rabbitTemplate.convertAndSend(
                    RabbitMqAutoConfiguration.NOTIFICATION_EXCHANGE,
                    RabbitMqAutoConfiguration.NOTIFICATION_SEND_KEY,
                    Map.of(
                            "userId", updateDto.getAssignedTo().toString(),
                            "title", "Назначение задачи",
                            "message", "Вам назначена задача: " + updatedTask.getTitle(),
                            "type", "TASK_ASSIGNED"
                    )
            );
        }

        return mapToDto(updatedTask);
    }

    /**
     * Изменить статус задачи
     */
    public TaskDto changeTaskStatus(UUID id, TaskStatus newStatus) {
        log.info("Changing task status to: {}", newStatus);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        task.setStatus(newStatus);
        task.setUpdatedAt(OffsetDateTime.now());

        Task updatedTask = taskRepository.save(task);

        // Publish task_completed event when task is done
        if (newStatus == TaskStatus.DONE) {
            ProjectEvent event = ProjectEvent.builder()
                    .taskId(updatedTask.getId())
                    .projectId(updatedTask.getProject().getId())
                    .userId(updatedTask.getAssignedTo())
                    .build();
            event.init("project-service");
            eventPublisher.publish(RabbitMqAutoConfiguration.PROJECT_EXCHANGE, "project.task_completed", event);
        }

        return mapToDto(updatedTask);
    }

    /**
     * Удалить задачу
     */
    public void deleteTask(UUID id) {
        log.info("Deleting task: {}", id);
        taskRepository.deleteById(id);
    }

    /**
     * Получить последние обновленные задачи пользователя (топ 10)
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getRecentUserTasks(UUID userId) {
        return taskRepository.findTop10ByAssignedToOrderByUpdatedAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .map(this::enrichWithUserName)
                .collect(Collectors.toList());
    }

    /**
     * Получить статистику задач пользователя
     */
    @Transactional(readOnly = true)
    public UserTaskStatsDto getUserTaskStats(UUID userId) {
        List<Task> tasks = taskRepository.findByAssignedTo(userId);
        int todo = 0, inProgress = 0, review = 0, done = 0, blocked = 0;
        for (Task t : tasks) {
            switch (t.getStatus()) {
                case TO_DO -> todo++;
                case IN_PROGRESS -> inProgress++;
                case REVIEW -> review++;
                case DONE -> done++;
                case BLOCKED -> blocked++;
            }
        }
        return UserTaskStatsDto.builder()
                .todoCount(todo)
                .inProgressCount(inProgress)
                .reviewCount(review)
                .doneCount(done)
                .blockedCount(blocked)
                .totalCount(tasks.size())
                .build();
    }

    /**
     * Снять назначение с задачи
     */
    public TaskDto unassignTask(UUID taskId) {
        log.info("Unassigning task: {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        task.setAssignedTo(null);
        task.setUpdatedAt(OffsetDateTime.now());

        Task updatedTask = taskRepository.save(task);
        return mapToDto(updatedTask);
    }

    private void validateProjectMembership(UUID projectId, UUID userId) {
        boolean isMember = projectMemberRepository.existsById(
                new ProjectMember.ProjectMemberId(projectId, userId)
        );
        if (!isMember) {
            throw new IllegalArgumentException("Пользователь не является участником проекта");
        }
    }

    private TaskDto enrichWithUserName(TaskDto dto) {
        if (dto.getAssignedTo() != null) {
            dto.setAssignedToName(userNameCacheService.fetchUserName(dto.getAssignedTo()));
        }
        return dto;
    }

    private TaskDto mapToDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .projectId(task.getProject().getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .assignedTo(task.getAssignedTo())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}