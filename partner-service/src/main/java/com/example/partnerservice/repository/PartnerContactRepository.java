package com.example.partnerservice.repository;

import com.example.partnerservice.model.PartnerContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PartnerContactRepository extends JpaRepository<PartnerContact, UUID> {
    List<PartnerContact> findByPartnerId(UUID partnerId);
    List<PartnerContact> findByPartnerIdAndIsPrimaryTrue(UUID partnerId);
}
