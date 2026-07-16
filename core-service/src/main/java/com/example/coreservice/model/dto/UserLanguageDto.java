package com.example.coreservice.model.dto;

import com.example.coreservice.model.enums.LanguageProficiency;

import java.util.UUID;

public record UserLanguageDto(
        UUID userId,
        String language,
        LanguageProficiency proficiency
) {
}