package com.example.documentservice.dto;

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
public class GeneratedDocumentDto {
    private UUID id;
    private UUID templateId;
    private String templateName;
    private UUID generatedFor;
    private String filePath;
    private OffsetDateTime generatedAt;
    private UUID generatedBy;
    private String parameters;
    private String status;
    private String errorMessage;
    private OffsetDateTime signedAt;
    private OffsetDateTime expiresAt;
}
