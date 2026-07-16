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
public class CreateEventTaskDto {
    private UUID eventId;
    private String title;
    private String description;
    private String difficulty;
    private Integer points;
    private OffsetDateTime deadline;
    private String filePath;
}