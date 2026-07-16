package com.example.coreservice.repository;

import com.example.coreservice.model.entity.EmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, UUID> {
}