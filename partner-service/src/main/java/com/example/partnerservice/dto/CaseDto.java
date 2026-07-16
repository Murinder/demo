package com.example.partnerservice.dto;

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
public class CaseDto {
    private UUID id;
    private UUID partnerId;
    private String title;
    private String description;
    private String difficulty;
    private List<String> requiredSkills;
    private String rewardDescription;
    private Boolean isActive;
    private String expectedDuration;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
