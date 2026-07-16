package com.example.eventservice.repository;

import com.example.eventservice.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, TeamMember.TeamMemberId> {

    @Query("SELECT COUNT(tm) > 0 FROM TeamMember tm WHERE tm.id.userId = :userId AND tm.team.event.id = :eventId")
    boolean existsByUserIdAndEventId(@Param("userId") UUID userId, @Param("eventId") UUID eventId);
}