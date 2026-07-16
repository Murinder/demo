package com.example.eventservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "event_tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventTask {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "difficulty", length = 50)
    private String difficulty;

    @Column(name = "points", nullable = false)
    private Integer points;

    @Column(name = "deadline")
    private OffsetDateTime deadline;

    @Column(name = "file_path", length = 512)
    private String filePath;

}