package com.example.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KpiValueDto {
    private UUID kpiId;
    private String kpiName;
    private BigDecimal value;
    private LocalDate period;
    private UUID entityId;
    private OffsetDateTime createdAt;
}
