package com.example.coreservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "lecturer_academic_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LecturerAcademicMetrics {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false)
    @Builder.Default
    private int publications = 0;

    @Column(nullable = false)
    @Builder.Default
    private int grants = 0;

    @Column(nullable = false)
    @Builder.Default
    private int hours = 0;

    @Column(nullable = false)
    @Builder.Default
    private int consultations = 0;

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private int monographs = 0;

    @Column(nullable = false)
    @Builder.Default
    private int articles = 0;

    @Column(nullable = false)
    @Builder.Default
    private int conferences = 0;

    @Column(name = "teaching_start_year")
    private Integer teachingStartYear;

    @Column(name = "supervised_phd", nullable = false)
    @Builder.Default
    private int supervisedPhd = 0;

    @Column(name = "supervised_masters", nullable = false)
    @Builder.Default
    private int supervisedMasters = 0;

    @Column(name = "supervised_bachelors", nullable = false)
    @Builder.Default
    private int supervisedBachelors = 0;
}
