package com.example.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportDto {
    private String reportType;
    private Map<String, Object> parameters;
    private String format;
    private Boolean scheduled;
}
