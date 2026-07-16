package com.example.analyticsservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "academic_performance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(AcademicPerformance.AcademicPerformanceId.class)
public class AcademicPerformance {

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Id
    @Column(nullable = false)
    private Integer semester;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal gpa;

    @Column(name = "credits_earned", nullable = false)
    private Integer creditsEarned;

    @Column(name = "courses_count", nullable = false)
    private Integer coursesCount;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AcademicPerformanceId implements Serializable {
        private UUID userId;
        private Integer semester;
    }
}
