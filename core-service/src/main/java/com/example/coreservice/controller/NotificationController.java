package com.example.coreservice.controller;

import com.example.sharedlib.response.ApiResponse;
import com.example.sharedlib.dto.NotificationDto;
import com.example.sharedlib.security.AuthenticatedOnly;
import com.example.coreservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Notifications", description = "Endpoints для управления уведомлениями")
public class NotificationController {
    private final NotificationService notificationService;

    /**
     * Получить все уведомления пользователя
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Получить уведомления пользователя", description = "Возвращает все уведомления пользователя в хронологическом порядке")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getUserNotifications(@PathVariable String userId) {
        log.info("Getting notifications for user: {}", userId);

        List<NotificationDto> notifications = notificationService.getUserNotifications(UUID.fromString(userId));

        return ResponseEntity.ok(ApiResponse.<List<NotificationDto>>builder()
                .success(true)
                .code("NOTIFICATIONS_RETRIEVED")
                .message("Notifications retrieved successfully")
                .data(notifications)
                .build());
    }

    /**
     * Получить непрочитанные уведомления
     */
    @GetMapping("/user/{userId}/unread")
    @Operation(summary = "Получить непрочитанные уведомления", description = "Возвращает только непрочитанные уведомления пользователя")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getUnreadNotifications(@PathVariable String userId) {
        log.info("Getting unread notifications for user: {}", userId);

        List<NotificationDto> notifications = notificationService.getUnreadNotifications(UUID.fromString(userId));

        return ResponseEntity.ok(ApiResponse.<List<NotificationDto>>builder()
                .success(true)
                .code("UNREAD_NOTIFICATIONS_RETRIEVED")
                .message("Unread notifications retrieved successfully")
                .data(notifications)
                .build());
    }

    /**
     * Получить количество непрочитанных уведомлений
     */
    @GetMapping("/user/{userId}/unread-count")
    @Operation(summary = "Получить количество непрочитанных уведомлений", description = "Возвращает количество непрочитанных уведомлений")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(@PathVariable String userId) {
        log.info("Getting unread count for user: {}", userId);

        long count = notificationService.getUnreadCount(UUID.fromString(userId));

        Map<String, Long> response = new HashMap<>();
        response.put("unreadCount", count);

        return ResponseEntity.ok(ApiResponse.<Map<String, Long>>builder()
                .success(true)
                .code("UNREAD_COUNT_RETRIEVED")
                .message("Unread count retrieved successfully")
                .data(response)
                .build());
    }

    /**
     * Отметить уведомление как прочитанное
     */
    @PutMapping("/{notificationId}/mark-read")
    @Operation(summary = "Отметить уведомление как прочитанное", description = "Отмечает уведомление как прочитанное")
    public ResponseEntity<ApiResponse<NotificationDto>> markAsRead(@PathVariable String notificationId) {
        log.info("Marking notification as read: {}", notificationId);

        NotificationDto notification = notificationService.markAsRead(UUID.fromString(notificationId));

        return ResponseEntity.ok(ApiResponse.<NotificationDto>builder()
                .success(true)
                .code("NOTIFICATION_MARKED_READ")
                .message("Notification marked as read successfully")
                .data(notification)
                .build());
    }
}