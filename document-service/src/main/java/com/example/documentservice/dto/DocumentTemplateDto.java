package com.example.documentservice.dto;

import com.example.documentservice.model.enums.DocumentType;
import com.example.documentservice.model.enums.TemplateStatus;
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
public class DocumentTemplateDto {
    private UUID id;
    private String name;
    private String description;
    private String filePath;
    private DocumentType documentType;
    private UUID createdBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private TemplateStatus status;
    private Integer version;
    private Boolean isPublic;
    private UUID facultyId;
    private UUID departmentId;
}
