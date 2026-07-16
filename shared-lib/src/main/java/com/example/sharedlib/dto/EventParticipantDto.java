package com.example.sharedlib.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventParticipantDto {
    private UUID eventId;
    private UUID userId;
    private String role;
    private OffsetDateTime registeredAt;
}