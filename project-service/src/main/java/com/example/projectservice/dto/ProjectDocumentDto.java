package com.example.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO для ProjectDocument сущности
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDocumentDto {
    private UUID id;
    private UUID projectId;
    private UUID taskId;
    private String filePath;
    private Integer version;
    private UUID uploadedBy;
    private OffsetDateTime createdAt;
    private String description;
}