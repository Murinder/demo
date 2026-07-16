package com.example.analyticsservice.repository;

import com.example.analyticsservice.model.KpiMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KpiMetricRepository extends JpaRepository<KpiMetric, UUID> {
    List<KpiMetric> findByCalculationMethod(KpiMetric.CalculationMethod calculationMethod);
    List<KpiMetric> findByIsCustomTrue();
}
