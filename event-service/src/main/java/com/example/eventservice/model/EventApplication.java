package com.example.eventservice.model;

import com.example.eventservice.model.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "event_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "team_id")
    private UUID teamId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "application_status")
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.SUBMITTED;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "motivation", columnDefinition = "TEXT")
    private String motivation;

    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;

    @Column(name = "updated_at")
    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @Column(name = "participant_role", length = 50)
    private String participantRole;

    @Column(name = "presentation_title", length = 255)
    private String presentationTitle;

    @Column(name = "presentation_description", columnDefinition = "TEXT")
    private String presentationDescription;
}