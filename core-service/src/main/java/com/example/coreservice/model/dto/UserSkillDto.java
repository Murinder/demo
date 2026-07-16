package com.example.coreservice.model.dto;

import java.util.UUID;

public record UserSkillDto(
        UUID userId,
        String skillName,
        int level,
        boolean verified
) {
}