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
public class DepartmentRatingDto {
    private UUID departmentId;
    private BigDecimal totalScore;
    private OffsetDateTime updatedAt;
    private Integer semester;
    private String calculationDetails;
    private UUID facultyId;
}
