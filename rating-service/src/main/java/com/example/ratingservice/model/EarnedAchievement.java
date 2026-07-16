package com.example.ratingservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "earned_achievements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EarnedAchievement {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "badge_key", nullable = false)
    private String badgeKey;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal score;

    @Column(nullable = false)
    private String category;

    @Column(name = "earned_at", nullable = false)
    private OffsetDateTime earnedAt;

    @Column(name = "related_entity_id")
    private UUID relatedEntityId;
}
