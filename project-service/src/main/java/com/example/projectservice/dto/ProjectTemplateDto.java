package com.example.projectservice.dto;

import com.example.projectservice.model.enums.TemplateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO для ProjectTemplate сущности
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTemplateDto {
    private UUID id;
    private String name;
    private String description;
    private TemplateType templateType;
    private String config;
    private UUID createdBy;
    private OffsetDateTime createdAt;
}