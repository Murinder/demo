package com.example.ratingservice.dto;

import com.example.ratingservice.model.RatingCriteriaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingCriteriaDto {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal weight;
    private Boolean isActive;
    private RatingCriteriaType criteriaType;
    private Integer basePoints;
    private Integer maxPoints;
}
