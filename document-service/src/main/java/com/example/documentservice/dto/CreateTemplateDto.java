package com.example.documentservice.dto;

import com.example.documentservice.model.enums.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTemplateDto {

    @NotBlank(message = "Template name is required")
    private String name;

    private String description;

    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    private Boolean isPublic;

    private UUID facultyId;

    private UUID departmentId;
}
