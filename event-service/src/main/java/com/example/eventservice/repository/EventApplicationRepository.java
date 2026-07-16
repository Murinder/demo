package com.example.eventservice.repository;

import com.example.eventservice.model.EventApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventApplicationRepository extends JpaRepository<EventApplication, UUID> {
    List<EventApplication> findByUserId(UUID userId);
    List<EventApplication> findByEvent_Id(UUID eventId);
    List<EventApplication> findByUserIdOrderByCreatedAtDesc(UUID userId);
    java.util.Optional<EventApplication> findByEvent_IdAndUserId(UUID eventId, UUID userId);
    List<EventApplication> findByEvent_IdAndTeamId(UUID eventId, UUID teamId);
    java.util.Optional<EventApplication> findByEvent_IdAndUserIdAndTeamId(UUID eventId, UUID userId, UUID teamId);
}