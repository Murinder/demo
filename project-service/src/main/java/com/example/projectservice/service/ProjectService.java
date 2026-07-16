package com.example.projectservice.service;

import com.example.projectservice.dto.TeacherProjectStatsDto;
import com.example.projectservice.dto.UserProjectStatsDto;
import com.example.projectservice.model.ProjectMember;
import com.example.projectservice.model.Task;
import com.example.projectservice.model.ProjectTemplate;
import com.example.projectservice.repository.ProjectMemberRepository;
import com.example.projectservice.repository.ProjectTemplateRepository;
import com.example.projectservice.repository.TaskRepository;
import com.example.sharedlib.dto.*;
import com.example.sharedlib.enums.ProjectRole;
import com.example.sharedlib.enums.TaskStatus;
import com.example.sharedlib.exception.ResourceNotFoundException;
import com.example.projectservice.model.Project;
import com.example.sharedlib.enums.ProjectStatus;
import com.example.projectservice.repository.ProjectRepository;
import com.example.sharedlib.config.RabbitMqAutoConfiguration;
import com.example.sharedlib.event.EventPublisher;
import com.example.sharedlib.event.ProjectEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;
    private final ProjectTemplateRepository projectTemplateRepository;
    private final ProjectMemberService projectMemberService;
    private final EventPublisher eventPublisher;

    /**
     * Создать новый проект
     */
    public ProjectDto createProject(ProjectDto projectDto) {
        log.info("Creating new project: {}", projectDto.getTitle());

        Project project = Project.builder()
                .id(UUID.randomUUID())
                .title(projectDto.getTitle())
                .description(projectDto.getDescription())
                .templateId(projectDto.getTemplateId())
                .status(ProjectStatus.ACTIVE)
                .startDate(projectDto.getStartDate())
                .endDate(projectDto.getEndDate())
                .createdBy(projectDto.getCreatedBy())
                .departmentId(projectDto.getDepartmentId())
                .build();

        Project savedProject = projectRepository.save(project);

        // Add creator as a project leader
        projectMemberService.addMember(savedProject, savedProject.getCreatedBy(), ProjectRole.LEADER);

        // Publish project.created event
        ProjectEvent event = ProjectEvent.builder()
                .projectId(savedProject.getId())
                .title(savedProject.getTitle())
                .createdBy(savedProject.getCreatedBy())
                .departmentId(savedProject.getDepartmentId())
                .build();
        event.init("project-service");
        eventPublisher.publish(RabbitMqAutoConfiguration.PROJECT_EXCHANGE, "project.created", event);

        return mapToDto(savedProject);
    }

    /**
     * Получить проект по ID
     */
    @Cacheable(value = "projects", key = "#id")
    @Transactional(readOnly = true)
    public ProjectDto getProjectById(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Project not found with id: {}", id);
                    return new ResourceNotFoundException("Project not found");
                });
        return mapToDto(project);
    }

    public Project getProjectEntityById(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Project not found with id: {}", id);
                    return new ResourceNotFoundException("Project not found");
                });
    }

    /**
     * Получить все проекты пользователя
     */
    @Transactional(readOnly = true)
    public List<ProjectDto> getUserProjects(UUID userId) {
        log.info("Getting projects for user: {}", userId);
        List<ProjectMember> memberships = projectMemberService.findProjectsByUserId(userId);
        List<UUID> projectIds = memberships.stream()
                .map(member -> member.getId().getProjectId())
                .collect(Collectors.toList());

        return projectRepository.findAllById(projectIds).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Получить активные проекты кафедры
     */
    @Transactional(readOnly = true)
    public List<ProjectDto> getDepartmentProjects(UUID departmentId) {
        return projectRepository.findByStatusAndDepartmentId(ProjectStatus.ACTIVE, departmentId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Обновить проект
     */
    @CacheEvict(value = "projects", key = "#id")
    public ProjectDto updateProject(UUID id, ProjectDto updateDto) {
        log.info("Updating project: {}", id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (updateDto.getTitle() != null) project.setTitle(updateDto.getTitle());
        if (updateDto.getDescription() != null) project.setDescription(updateDto.getDescription());
        if (updateDto.getStatus() != null) project.setStatus(updateDto.getStatus());
        if (updateDto.getEndDate() != null) project.setEndDate(updateDto.getEndDate());

        project.setUpdatedAt(OffsetDateTime.now());

        Project updatedProject = projectRepository.save(project);
        return mapToDto(updatedProject);
    }

    /**
     * Изменить статус проекта
     */
    @CacheEvict(value = "projects", key = "#id")
    public ProjectDto changeProjectStatus(UUID id, ProjectStatus newStatus) {
        log.info("Changing project status to: {}", newStatus);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        String oldStatus = project.getStatus().name();
        project.setStatus(newStatus);
        project.setUpdatedAt(OffsetDateTime.now());

        Project updatedProject = projectRepository.save(project);

        // Publish project.status_changed event
        ProjectEvent event = ProjectEvent.builder()
                .projectId(updatedProject.getId())
                .oldStatus(oldStatus)
                .newStatus(newStatus.name())
                .build();
        event.init("project-service");
        eventPublisher.publish(RabbitMqAutoConfiguration.PROJECT_EXCHANGE, "project.status_changed", event);

        return mapToDto(updatedProject);
    }

    /**
     * Удалить проект
     */
    @CacheEvict(value = "projects", key = "#id")
    public void deleteProject(UUID id) {
        log.info("Deleting project: {}", id);
        projectRepository.deleteById(id);
    }

    /**
     * Получить статистику проектов пользователя
     */
    @Transactional(readOnly = true)
    public UserProjectStatsDto getUserProjectStats(UUID userId) {
        List<ProjectDto> projects = getUserProjects(userId);
        int active = 0, completed = 0, frozen = 0;
        for (ProjectDto p : projects) {
            if (p.getStatus() == ProjectStatus.ACTIVE) active++;
            else if (p.getStatus() == ProjectStatus.COMPLETED) completed++;
            else if (p.getStatus() == ProjectStatus.FROZEN) frozen++;
        }
        return UserProjectStatsDto.builder()
                .activeCount(active)
                .completedCount(completed)
                .frozenCount(frozen)
                .totalCount(projects.size())
                .build();
    }

    /**
     * Получить детальную статистику проектов пользователя с задачами и участниками
     */
    @Transactional(readOnly = true)
    public ProjectDashboardStatsDto getUserProjectDetailedStats(UUID userId) {
        List<ProjectMember> memberships = projectMemberService.findProjectsByUserId(userId);
        List<UUID> projectIds = memberships.stream()
                .map(m -> m.getId().getProjectId())
                .collect(Collectors.toList());

        List<Project> projects = projectRepository.findAllById(projectIds);
        List<ProjectWithTaskSummary> summaries = projects.stream()
                .map(this::buildProjectWithTaskSummary)
                .collect(Collectors.toList());

        return ProjectDashboardStatsDto.builder().projects(summaries).build();
    }

    /**
     * Получить дашборд кафедры
     */
    @Transactional(readOnly = true)
    public DepartmentDashboardDto getDepartmentDashboard(UUID departmentId) {
        List<Project> projects = projectRepository.findByDepartmentId(departmentId);

        int active = 0, completed = 0;
        for (Project p : projects) {
            if (p.getStatus() == ProjectStatus.ACTIVE) active++;
            else if (p.getStatus() == ProjectStatus.COMPLETED) completed++;
        }

        List<ProjectWithTaskSummary> summaries = projects.stream()
                .map(this::buildProjectWithTaskSummary)
                .collect(Collectors.toList());

        // Projects created per month (last 6 months)
        Map<String, Integer> projectsByMonth = new LinkedHashMap<>();
        YearMonth now = YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            YearMonth month = now.minusMonths(i);
            String label = month.getMonth().getDisplayName(TextStyle.SHORT, new Locale("ru"));
            projectsByMonth.put(label, 0);
        }
        for (Project p : projects) {
            if (p.getCreatedAt() != null) {
                YearMonth created = YearMonth.from(p.getCreatedAt());
                if (!created.isBefore(now.minusMonths(5))) {
                    String label = created.getMonth().getDisplayName(TextStyle.SHORT, new Locale("ru"));
                    projectsByMonth.merge(label, 1, Integer::sum);
                }
            }
        }

        return DepartmentDashboardDto.builder()
                .totalProjects(projects.size())
                .activeProjects(active)
                .completedProjects(completed)
                .projects(summaries)
                .projectsByMonth(projectsByMonth)
                .build();
    }

    private ProjectWithTaskSummary buildProjectWithTaskSummary(Project project) {
        List<Task> tasks = project.getTasks();
        int done = 0, review = 0, blocked = 0;
        OffsetDateTime lastUpdate = null;

        for (Task t : tasks) {
            if (t.getStatus() == TaskStatus.DONE) done++;
            else if (t.getStatus() == TaskStatus.REVIEW) review++;
            else if (t.getStatus() == TaskStatus.BLOCKED) blocked++;
            if (t.getUpdatedAt() != null && (lastUpdate == null || t.getUpdatedAt().isAfter(lastUpdate))) {
                lastUpdate = t.getUpdatedAt();
            }
        }

        // Build per-member task summaries
        Map<UUID, MemberTaskSummary> memberMap = new LinkedHashMap<>();
        for (ProjectMember pm : project.getMembers()) {
            memberMap.put(pm.getId().getUserId(), MemberTaskSummary.builder()
                    .userId(pm.getId().getUserId())
                    .tasksDone(0).tasksTotal(0).build());
        }
        for (Task t : tasks) {
            if (t.getAssignedTo() != null && memberMap.containsKey(t.getAssignedTo())) {
                MemberTaskSummary ms = memberMap.get(t.getAssignedTo());
                ms.setTasksTotal(ms.getTasksTotal() + 1);
                if (t.getStatus() == TaskStatus.DONE) {
                    ms.setTasksDone(ms.getTasksDone() + 1);
                }
                if (t.getUpdatedAt() != null && (ms.getLastActive() == null || t.getUpdatedAt().isAfter(ms.getLastActive()))) {
                    ms.setLastActive(t.getUpdatedAt());
                }
            }
        }

        return ProjectWithTaskSummary.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .projectType(project.getProjectType())
                .createdBy(project.getCreatedBy())
                .status(project.getStatus().name())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .memberCount(project.getMembers().size())
                .tasksDone(done)
                .tasksTotal(tasks.size())
                .tasksReview(review)
                .tasksBlocked(blocked)
                .lastTaskUpdate(lastUpdate)
                .members(new ArrayList<>(memberMap.values()))
                .build();
    }

    /**
     * Получить статистику преподавателей по проектам (batch)
     */
    @Transactional(readOnly = true)
    public List<TeacherProjectStatsDto> getBatchTeacherStats(List<UUID> teacherIds) {
        return teacherIds.stream().map(teacherId -> {
            UserProjectStatsDto projectStats = getUserProjectStats(teacherId);
            long totalStudents = projectMemberRepository.countStudentsByMentor(teacherId);
            long activeStudents = projectMemberRepository.countActiveStudentsByMentor(teacherId);

            return TeacherProjectStatsDto.builder()
                    .userId(teacherId)
                    .activeCount(projectStats.getActiveCount())
                    .completedCount(projectStats.getCompletedCount())
                    .totalCount(projectStats.getTotalCount())
                    .totalStudents((int) totalStudents)
                    .activeStudents((int) activeStudents)
                    .build();
        }).collect(Collectors.toList());
    }

    private ProjectDto mapToDto(Project project) {
        String templateType = null;
        if (project.getTemplateId() != null) {
            templateType = projectTemplateRepository.findById(project.getTemplateId())
                    .map(t -> t.getTemplateType().name())
                    .orElse(null);
        }

        long totalTasks = taskRepository.countByProjectId(project.getId());
        long doneTasks = taskRepository.countByProjectIdAndStatus(project.getId(), TaskStatus.DONE);

        return ProjectDto.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .templateId(project.getTemplateId())
                .status(project.getStatus())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .createdBy(project.getCreatedBy())
                .departmentId(project.getDepartmentId())
                .projectType(project.getProjectType())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .templateType(templateType)
                .taskCount((int) totalTasks)
                .completedTaskCount((int) doneTasks)
                .build();
    }
}