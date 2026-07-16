package com.example.eventservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventApplicationDto {
    private UUID id;
    private UUID eventId;
    private UUID userId;
    private String status;
    private String motivation;
    private String skills;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String participantRole;
    private String presentationTitle;
    private String presentationDescription;
    private UUID teamId;
    private String userName;
    private String teamName;
}