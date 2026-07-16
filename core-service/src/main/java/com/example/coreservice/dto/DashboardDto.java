package com.example.coreservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class DashboardDto {
    private UUID userId;
    private String widgetConfig;
}