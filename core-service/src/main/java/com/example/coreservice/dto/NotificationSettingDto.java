package com.example.coreservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSettingDto {
    private UUID userId;
    private boolean emailEnabled;
    private boolean inAppEnabled;
    private boolean pushEnabled;
    private OffsetDateTime updatedAt;
}