package com.example.eventservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;


import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMember {

    @EmbeddedId
    private TeamMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("teamId")
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String role = "member";

    @Column(name = "joined_at")
    @Builder.Default
    private OffsetDateTime joinedAt = OffsetDateTime.now();

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamMemberId implements Serializable {

        @Column(name = "team_id")
        private UUID teamId;

        @Column(name = "user_id")
        private UUID userId;
    }
}