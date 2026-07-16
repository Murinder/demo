package com.example.ratingservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "rating_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingSettings {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "semester")
    private Integer semester;

    @Column(name = "calculation_algorithm")
    private String calculationAlgorithm;

    @Column(name = "last_calculation")
    private OffsetDateTime lastCalculation;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "parameters", columnDefinition = "jsonb")
    private String parameters;
}