package com.example.projectservice.controller;

import com.example.sharedlib.response.ApiResponse;
import com.example.sharedlib.dto.TaskDto;
import com.example.sharedlib.enums.TaskStatus;
import com.example.sharedlib.security.AuthenticatedOnly;
import com.example.projectservice.dto.UserTaskStatsDto;
import com.example.projectservice.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Tasks", description = "Endpoints для управления задачами проектов")
public class TaskController {
    private final TaskService taskService;

    /**
     * Создать новую задачу
     */
    @PostMapping
    @Operation(summary = "Создать новую задачу", description = "Создает новую задачу в проекте")
    public ResponseEntity<ApiResponse<TaskDto>> createTask(@RequestBody TaskDto taskDto) {
        log.info("Creating new task: {}", taskDto.getTitle());

        TaskDto createdTask = taskService.createTask(taskDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<TaskDto>builder()
                        .success(true)
                        .code("TASK_CREATED")
                        .message("Task created successfully")
                        .data(createdTask)
                        .build());
    }

    /**
     * Получить задачу по ID
     */
    @GetMapping("/{taskId}")
    @Operation(summary = "Получить задачу", description = "Возвращает информацию о задаче")
    public ResponseEntity<ApiResponse<TaskDto>> getTask(@PathVariable UUID taskId) {
        log.info("Getting task: {}", taskId);

        TaskDto task = taskService.getTaskById(taskId);

        return ResponseEntity.ok(ApiResponse.<TaskDto>builder()
                .success(true)
                .code("TASK_RETRIEVED")
                .message("Task retrieved successfully")
                .data(task)
                .build());
    }

    /**
     * Получить все задачи проекта
     */
    @GetMapping("/project/{projectId}")
    @Operation(summary = "Получить задачи проекта", description = "Возвращает все задачи проекта")
    public ResponseEntity<ApiResponse<List<TaskDto>>> getProjectTasks(@PathVariable UUID projectId) {
        log.info("Getting tasks for project: {}", projectId);

        List<TaskDto> tasks = taskService.getProjectTasks(projectId);

        return ResponseEntity.ok(ApiResponse.<List<TaskDto>>builder()
                .success(true)
                .code("PROJECT_TASKS_RETRIEVED")
                .message("Project tasks retrieved successfully")
                .data(tasks)
                .build());
    }

    /**
     * Получить задачи, назначенные пользователю
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Получить задачи пользователя", description = "Возвращает все задачи, назначенные пользователю")
    public ResponseEntity<ApiResponse<List<TaskDto>>> getUserTasks(@PathVariable UUID userId) {
        log.info("Getting tasks for user: {}", userId);

        List<TaskDto> tasks = taskService.getUserTasks(userId);

        return ResponseEntity.ok(ApiResponse.<List<TaskDto>>builder()
                .success(true)
                .code("USER_TASKS_RETRIEVED")
                .message("User tasks retrieved successfully")
                .data(tasks)
                .build());
    }

    /**
     * Обновить задачу
     */
    @PutMapping("/{taskId}")
    @Operation(summary = "Обновить задачу", description = "Обновляет информацию о задаче")
    public ResponseEntity<ApiResponse<TaskDto>> updateTask(
            @PathVariable UUID taskId,
            @RequestBody TaskDto updateDto) {
        log.info("Updating task: {}", taskId);

        TaskDto updatedTask = taskService.updateTask(taskId, updateDto);

        return ResponseEntity.ok(ApiResponse.<TaskDto>builder()
                .success(true)
                .code("TASK_UPDATED")
                .message("Task updated successfully")
                .data(updatedTask)
                .build());
    }

    /**
     * Получить последние обновленные задачи пользователя
     */
    @GetMapping("/user/{userId}/recent")
    @Operation(summary = "Последние задачи пользователя", description = "Возвращает 10 последних обновленных задач")
    public ResponseEntity<ApiResponse<List<TaskDto>>> getRecentUserTasks(@PathVariable UUID userId) {
        log.info("Getting recent tasks for user: {}", userId);
        List<TaskDto> tasks = taskService.getRecentUserTasks(userId);
        return ResponseEntity.ok(ApiResponse.<List<TaskDto>>builder()
                .success(true)
                .code("RECENT_TASKS_RETRIEVED")
                .message("Recent tasks retrieved successfully")
                .data(tasks)
                .build());
    }

    /**
     * Получить статистику задач пользователя
     */
    @GetMapping("/user/{userId}/stats")
    @Operation(summary = "Статистика задач пользователя", description = "Возвращает количество задач по статусам")
    public ResponseEntity<ApiResponse<UserTaskStatsDto>> getUserTaskStats(@PathVariable UUID userId) {
        log.info("Getting task stats for user: {}", userId);
        UserTaskStatsDto stats = taskService.getUserTaskStats(userId);
        return ResponseEntity.ok(ApiResponse.<UserTaskStatsDto>builder()
                .success(true)
                .code("TASK_STATS_RETRIEVED")
                .message("Task stats retrieved successfully")
                .data(stats)
                .build());
    }

    /**
     * Частично обновить задачу (PATCH)
     */
    @PatchMapping("/{taskId}")
    @Operation(summary = "Частично обновить задачу", description = "Обновляет только переданные поля задачи")
    public ResponseEntity<ApiResponse<TaskDto>> patchTask(
            @PathVariable UUID taskId,
            @RequestBody TaskDto updateDto) {
        log.info("Patching task: {}", taskId);

        TaskDto updatedTask = taskService.updateTask(taskId, updateDto);

        return ResponseEntity.ok(ApiResponse.<TaskDto>builder()
                .success(true)
                .code("TASK_UPDATED")
                .message("Task updated successfully")
                .data(updatedTask)
                .build());
    }

    /**
     * Изменить статус задачи
     */
    @PatchMapping("/{taskId}/status")
    @Operation(summary = "Изменить статус задачи", description = "Изменяет статус задачи на Kanban-доске")
    public ResponseEntity<ApiResponse<TaskDto>> changeTaskStatus(
            @PathVariable UUID taskId,
            @RequestParam TaskStatus status) {
        log.info("Changing task status: {}", taskId);

        TaskDto updatedTask = taskService.changeTaskStatus(taskId, status);

        return ResponseEntity.ok(ApiResponse.<TaskDto>builder()
                .success(true)
                .code("TASK_STATUS_CHANGED")
                .message("Task status changed successfully")
                .data(updatedTask)
                .build());
    }

    /**
     * Снять назначение с задачи
     */
    @DeleteMapping("/{taskId}/assignee")
    @Operation(summary = "Снять назначение", description = "Убирает исполнителя с задачи")
    public ResponseEntity<ApiResponse<TaskDto>> unassignTask(@PathVariable UUID taskId) {
        log.info("Unassigning task: {}", taskId);

        TaskDto updatedTask = taskService.unassignTask(taskId);

        return ResponseEntity.ok(ApiResponse.<TaskDto>builder()
                .success(true)
                .code("TASK_UNASSIGNED")
                .message("Task unassigned successfully")
                .data(updatedTask)
                .build());
    }

    /**
     * Удалить задачу
     */
    @DeleteMapping("/{taskId}")
    @Operation(summary = "Удалить задачу", description = "Удаляет задачу из проекта")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable UUID taskId) {
        log.info("Deleting task: {}", taskId);

        taskService.deleteTask(taskId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .code("TASK_DELETED")
                .message("Task deleted successfully")
                .build());
    }
}