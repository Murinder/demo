package com.example.partnerservice.repository;

import com.example.partnerservice.model.Agreement;
import com.example.partnerservice.model.enums.AgreementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AgreementRepository extends JpaRepository<Agreement, UUID> {
    List<Agreement> findByPartnerId(UUID partnerId);
    List<Agreement> findByStatus(AgreementStatus status);
    List<Agreement> findByProjectId(UUID projectId);
}
