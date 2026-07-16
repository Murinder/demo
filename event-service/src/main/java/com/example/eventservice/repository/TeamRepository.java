package com.example.eventservice.repository;

import com.example.eventservice.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {
    List<Team> findByEvent_Id(UUID eventId);

    boolean existsByEvent_IdAndCreatedBy(UUID eventId, UUID createdBy);

    @Query("SELECT DISTINCT t FROM Team t LEFT JOIN FETCH t.members WHERE t.event.id = :eventId")
    List<Team> findByEventIdWithMembers(@Param("eventId") UUID eventId);
}