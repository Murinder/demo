package com.example.coreservice.model.dto;

import com.example.coreservice.model.enums.LinkType;

import java.util.UUID;

public record UserLinkDto(
        UUID userId,
        LinkType linkType,
        String url
) {
}