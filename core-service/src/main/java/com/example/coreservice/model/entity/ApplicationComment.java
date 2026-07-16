package com.example.coreservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "application_comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationComment {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "application_id", nullable = false)
    private UUID applicationId;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Column(name = "author_role", nullable = false, length = 50)
    private String authorRole;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
