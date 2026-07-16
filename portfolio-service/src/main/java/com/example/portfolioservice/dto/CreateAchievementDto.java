package com.example.portfolioservice.dto;

import com.example.portfolioservice.model.Achievement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAchievementDto {
    private Achievement.AchievementType type;
    private String title;
    private String description;
    private LocalDate date;
    private String proofDocument;
    private Boolean isExternal;
    private String issuer;
}
