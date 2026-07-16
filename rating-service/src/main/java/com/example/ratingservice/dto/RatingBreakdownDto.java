package com.example.ratingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingBreakdownDto {
    private UUID userId;
    private BigDecimal totalScore;
    private String verificationStatus;
    private Integer semester;
    private OffsetDateTime updatedAt;
    private BigDecimal academicScore;
    private BigDecimal activityScore;
    private BigDecimal achievementsScore;
    private BigDecimal leadershipScore;
    private BigDecimal projectsScore;
    private BigDecimal innovationScore;
    private BigDecimal monthGrowth;
    private BigDecimal rawScore;
    private Map<String, List<SubParameterDto>> subParameters;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubParameterDto {
        private String name;
        private BigDecimal score;
        private long count;
    }
}
