package com.example.ratingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingHistoryDto {
    private UUID id;
    private UUID userId;
    private UUID departmentId;
    private BigDecimal score;
    private OffsetDateTime calculatedAt;
    private String reason;
    private UUID criteriaId;
    private UUID relatedEntityId;
    private Integer semester;
}
