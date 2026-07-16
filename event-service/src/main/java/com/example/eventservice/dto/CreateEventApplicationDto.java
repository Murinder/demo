package com.example.eventservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEventApplicationDto {
    private UUID eventId;
    private UUID userId;
    private String motivation;
    private String skills;
    private String participantRole;
    private String presentationTitle;
    private String presentationDescription;
    private UUID teamId;
    private String teamName;
}