package com.example.ratingservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "department_ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentRating {
    @Id
    @UuidGenerator
    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "total_score", precision = 10, scale = 2)
    private BigDecimal totalScore = BigDecimal.ZERO;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @Column(name = "semester")
    private Integer semester;

    @Column(name = "calculation_details", columnDefinition = "jsonb")
    private String calculationDetails;

    @Column(name = "faculty_id")
    private UUID facultyId;
}