package com.example.ratingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRatingDto {
    private UUID userId;
    private String userName;
    private BigDecimal totalScore;
    private String calculationDetails;
    private OffsetDateTime updatedAt;
    private Integer semester;
    private String verificationStatus;
}
