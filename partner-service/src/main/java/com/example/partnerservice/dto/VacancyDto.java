package com.example.partnerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VacancyDto {
    private UUID id;
    private UUID partnerId;
    private String title;
    private String description;
    private String requirements;
    private Boolean isActive;
    private String vacancyType;
    private String location;
    private String salaryRange;
    private LocalDate applicationDeadline;
    private UUID contactId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
