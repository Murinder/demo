package com.example.portfolioservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "academic_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicInfo {
    @Id
    @Column(name = "portfolio_id")
    private UUID portfolioId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "portfolio_id", referencedColumnName = "user_id")
    private Portfolio portfolio;

    @Column(name = "faculty")
    private String faculty;

    @Column(name = "department")
    private String department;

    @Column(name = "study_program")
    private String studyProgram;

    @Column(name = "group_name", length = 50)
    private String groupName;

    @Column(name = "enrollment_year")
    private Integer enrollmentYear;

    @Column(name = "current_semester")
    private Integer currentSemester;

    @Column(name = "gpa", precision = 4, scale = 2)
    private BigDecimal gpa;

    @Column(name = "credits_earned")
    private Integer creditsEarned;

    @Column(name = "progress_percentage", precision = 5, scale = 2)
    private BigDecimal progressPercentage;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt = OffsetDateTime.now();
}
