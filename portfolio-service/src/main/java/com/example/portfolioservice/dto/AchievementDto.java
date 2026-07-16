package com.example.portfolioservice.dto;

import com.example.portfolioservice.model.Achievement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementDto {
    private UUID id;
    private UUID portfolioId;
    private Achievement.AchievementType type;
    private String title;
    private String description;
    private LocalDate date;
    private String proofDocument;
    private Boolean isExternal;
    private String issuer;
    private OffsetDateTime createdAt;
}
