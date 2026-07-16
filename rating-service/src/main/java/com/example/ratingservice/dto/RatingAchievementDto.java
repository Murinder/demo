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
public class RatingAchievementDto {
    private UUID id;
    private String title;
    private String description;
    private BigDecimal score;
    private String category;
    private OffsetDateTime earnedAt;
    private UUID relatedEntityId;
}
