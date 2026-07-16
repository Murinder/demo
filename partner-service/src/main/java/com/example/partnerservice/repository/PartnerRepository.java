package com.example.partnerservice.repository;

import com.example.partnerservice.model.Partner;
import com.example.partnerservice.model.enums.PartnershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, UUID> {
    List<Partner> findByIsActiveTrue();
    List<Partner> findByPartnershipStatus(PartnershipStatus status);
    List<Partner> findByIndustry(String industry);
}
