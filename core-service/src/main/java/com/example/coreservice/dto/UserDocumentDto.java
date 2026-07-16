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
public class UserDocumentDto {
    private UUID id;
    private UUID userId;
    private String filePath;
    private String fileName;
    private String description;
    private OffsetDateTime uploadedAt;
}
