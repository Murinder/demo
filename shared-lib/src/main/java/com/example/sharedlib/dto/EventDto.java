package com.example.sharedlib.dto;

import com.example.sharedlib.enums.EventStatus;
import com.example.sharedlib.enums.EventFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO для Event сущности
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {
    private UUID id;
    private String title;
    private String description;
    private EventStatus status;
    private EventFormat format;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private Integer maxParticipants;
    private UUID createdBy;
    private String eventType;
    private UUID departmentId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}