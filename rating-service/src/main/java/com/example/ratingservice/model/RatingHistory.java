package com.example.ratingservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * RatingHistory entity
 */
@Entity
@Table(name = "rating_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingHistory {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "score", precision = 10, scale = 2)
    private BigDecimal score;

    @Column(name = "calculated_at")
    private OffsetDateTime calculatedAt = OffsetDateTime.now();

    @Column(name = "reason")
    private String reason;

    @Column(name = "criteria_id")
    private UUID criteriaId;

    @Column(name = "related_entity_id")
    private UUID relatedEntityId;

    @Column(name = "semester")
    private Integer semester;
}