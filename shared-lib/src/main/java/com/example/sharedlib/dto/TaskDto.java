package com.example.sharedlib.dto;

import com.example.sharedlib.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO для Task сущности
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {
    private UUID id;
    private UUID projectId;
    private String title;
    private String description;
    private TaskStatus status;
    private UUID assignedTo;
    private String assignedToName;
    private LocalDate dueDate;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}