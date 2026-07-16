package com.example.partnerservice.repository;

import com.example.partnerservice.model.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CaseRepository extends JpaRepository<Case, UUID> {
    List<Case> findByPartnerId(UUID partnerId);
    List<Case> findByIsActiveTrue();
    List<Case> findByDifficulty(String difficulty);
}
