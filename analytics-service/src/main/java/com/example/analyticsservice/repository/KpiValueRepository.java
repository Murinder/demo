package com.example.analyticsservice.repository;

import com.example.analyticsservice.model.KpiValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KpiValueRepository extends JpaRepository<KpiValue, KpiValue.KpiValueId> {
    List<KpiValue> findByKpiId(UUID kpiId);
    List<KpiValue> findByEntityId(UUID entityId);
    List<KpiValue> findByKpiIdAndEntityId(UUID kpiId, UUID entityId);
}
