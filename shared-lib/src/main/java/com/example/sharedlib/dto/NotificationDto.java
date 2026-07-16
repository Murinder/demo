package com.example.sharedlib.dto;

import com.example.sharedlib.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO для Notification сущности
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private UUID id;
    private UUID userId;
    private NotificationType type;
    private String title;
    private String message;
    private Boolean isRead;
    private OffsetDateTime createdAt;
    private OffsetDateTime readAt;
}