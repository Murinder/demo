package com.example.documentservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateDocumentRequest {

    @NotNull(message = "Template ID is required")
    private UUID templateId;

    @NotNull(message = "Generated for user ID is required")
    private UUID generatedFor;

    private Map<String, String> parameters;
}
