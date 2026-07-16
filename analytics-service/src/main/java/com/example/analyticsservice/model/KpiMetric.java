package com.example.analyticsservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "kpi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KpiMetric {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "calculation_method", nullable = false, columnDefinition = "kpi_calculation_method")
    private CalculationMethod calculationMethod;

    @Builder.Default
    @Column(name = "is_custom")
    private Boolean isCustom = false;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @Column
    private String formula;

    @Column(name = "target_value", precision = 10, scale = 2)
    private BigDecimal targetValue;

    public enum CalculationMethod {
        PROJECT_COMPLETION, STUDENT_ENGAGEMENT, DEPARTMENT_ACTIVITY, EVENT_SUCCESS, CUSTOM
    }
}
