package com.example.eventservice.dto;

import com.example.sharedlib.enums.EventFormat;
import com.example.sharedlib.enums.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEventDto {
    private String title;
    private String description;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private EventFormat format;
    private String rules;
    private BigDecimal budget;
    private String evaluationCriteria;
    private UUID createdBy;
    private String eventType;
    private String location;
    private String organizerName;
    private EventStatus status;
    private Integer maxParticipants;
    private OffsetDateTime registrationDeadline;
}