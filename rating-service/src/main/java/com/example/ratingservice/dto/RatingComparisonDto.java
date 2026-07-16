package com.example.ratingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingComparisonDto {
    private UUID userId;
    private String userName;
    private BigDecimal userAcademic;
    private BigDecimal userActivity;
    private BigDecimal userAchievements;
    private BigDecimal userTotal;
    private BigDecimal avgAcademic;
    private BigDecimal avgActivity;
    private BigDecimal avgAchievements;
    private BigDecimal avgTotal;
    private BigDecimal groupAvg;
    private BigDecimal departmentAvg;
    private BigDecimal facultyAvg;
}
