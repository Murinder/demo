package com.example.eventservice.dto;

import com.example.sharedlib.enums.EventFormat;
import com.example.sharedlib.enums.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventDto {
    private String title;
    private String description;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private EventFormat format;
    private EventStatus status;
    private String rules;
    private BigDecimal budget;
    private String evaluationCriteria;
    private String eventType;
    private String location;
    private Integer maxParticipants;
    private OffsetDateTime registrationDeadline;
}