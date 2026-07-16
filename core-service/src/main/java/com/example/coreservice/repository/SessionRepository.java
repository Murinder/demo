package com.example.coreservice.repository;

import com.example.coreservice.model.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {
}