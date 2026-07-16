package com.example.eventservice.model;

import com.example.eventservice.model.enums.DefenseStatus;
import com.example.eventservice.model.enums.DefenseType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "defenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Defense {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "student_name")
    private String studentName;

    @Column(name = "supervisor_id", nullable = false)
    private UUID supervisorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "defense_type", nullable = false)
    private DefenseType defenseType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DefenseStatus status = DefenseStatus.PLANNED;

    @Column(name = "project_title")
    private String projectTitle;

    @Column(name = "defense_date")
    private LocalDate defenseDate;

    @Column(name = "defense_time")
    private String defenseTime;

    @Column
    private String room;

    @Column
    private Integer grade;

    @Column(name = "reviewers_count")
    private Integer reviewersCount;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
