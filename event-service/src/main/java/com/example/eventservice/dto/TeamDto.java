package com.example.eventservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamDto {
    private UUID id;
    private UUID eventId;
    private String name;
    private UUID createdBy;
    private OffsetDateTime createdAt;
    private List<UUID> memberUserIds;
    private List<TeamMemberDetailDto> members;
}