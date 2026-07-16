package com.example.coreservice.model.dto;

import com.example.coreservice.model.enums.SessionStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SessionDto(
        UUID id,
        UUID userId,
        String token,
        OffsetDateTime expiresAt,
        OffsetDateTime createdAt,
        String ipAddress,
        String userAgent,
        SessionStatus status
) {
}