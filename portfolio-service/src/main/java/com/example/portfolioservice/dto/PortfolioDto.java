package com.example.portfolioservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDto {
    private UUID userId;
    private String visibilitySettings;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<AchievementDto> achievements;
    private List<PortfolioSkillDto> skills;
    private List<ReviewDto> reviews;
}
