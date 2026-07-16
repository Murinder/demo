package com.example.eventservice.repository;

import com.example.eventservice.model.Defense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DefenseRepository extends JpaRepository<Defense, UUID> {
    List<Defense> findBySupervisorIdOrderByDefenseDateAsc(UUID supervisorId);
    List<Defense> findByStudentIdOrderByDefenseDateAsc(UUID studentId);
}
