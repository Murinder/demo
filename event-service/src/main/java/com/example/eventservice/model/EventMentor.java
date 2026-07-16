package com.example.eventservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "event_mentors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventMentor {

    @EmbeddedId
    private EventMentorId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "expertise")
    private String expertise;
    @Column(name = "availability")
    private String availability;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public static class EventMentorId implements Serializable {

    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "user_id")
    private UUID userId;
}
}