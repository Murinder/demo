package com.example.coreservice.repository;

import com.example.coreservice.model.entity.LecturerAcademicMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LecturerAcademicMetricsRepository extends JpaRepository<LecturerAcademicMetrics, UUID> {
    List<LecturerAcademicMetrics> findByUserIdIn(List<UUID> userIds);
}
