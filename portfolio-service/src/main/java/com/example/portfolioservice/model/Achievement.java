package com.example.portfolioservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "achievements", indexes = {
        @Index(name = "idx_achievements_portfolio_id", columnList = "portfolio_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Achievement {
    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", referencedColumnName = "user_id", nullable = false)
    private Portfolio portfolio;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "type", nullable = false, columnDefinition = "achievement_type")
    private AchievementType type;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "proof_document", length = 512)
    private String proofDocument;

    @Column(name = "is_external")
    private Boolean isExternal = false;

    @Column(name = "issuer")
    private String issuer;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public enum AchievementType {
        PROJECT, EVENT, CERTIFICATE, PUBLICATION, GRANT, EXTERNAL
    }
}
