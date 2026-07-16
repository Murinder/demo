package com.example.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
    private UUID id;
    private String reportType;
    private Map<String, Object> parameters;
    private OffsetDateTime generatedAt;
    private String filePath;
    private Boolean scheduled;
    private UUID createdBy;
    private String format;
    private String status;
    private String errorMessage;
}
