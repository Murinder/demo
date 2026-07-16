package com.example.portfolioservice.dto;

import com.example.portfolioservice.model.PortfolioSkill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioSkillDto {
    private UUID id;
    private UUID portfolioId;
    private String name;
    private Integer level;
    private PortfolioSkill.VerificationStatus verificationStatus;
    private UUID verifiedBy;
    private OffsetDateTime verifiedAt;
    private OffsetDateTime createdAt;
}
