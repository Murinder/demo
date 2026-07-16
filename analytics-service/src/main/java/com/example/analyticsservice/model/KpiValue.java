package com.example.analyticsservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "kpi_values")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(KpiValue.KpiValueId.class)
public class KpiValue {

    @Id
    @Column(name = "kpi_id", nullable = false)
    private UUID kpiId;

    @Id
    @Column(nullable = false)
    private LocalDate period;

    @Id
    @Column(name = "entity_id")
    private UUID entityId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kpi_id", insertable = false, updatable = false)
    private KpiMetric kpi;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KpiValueId implements Serializable {
        private UUID kpiId;
        private LocalDate period;
        private UUID entityId;
    }
}
