package com.example.sharedlib.dto;

import com.example.sharedlib.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO для Project сущности
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDto {
    private UUID id;
    private String title;
    private String description;
    private UUID templateId;
    private ProjectStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private UUID createdBy;
    private UUID departmentId;
    private String projectType;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // Resolved from template
    private String templateType;

    // Task statistics
    private Integer taskCount;
    private Integer completedTaskCount;
}