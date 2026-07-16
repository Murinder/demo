package com.example.analyticsservice.repository;

import com.example.analyticsservice.model.AcademicPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AcademicPerformanceRepository extends JpaRepository<AcademicPerformance, AcademicPerformance.AcademicPerformanceId> {
    List<AcademicPerformance> findByUserId(UUID userId);
}
