package com.example.partnerservice.model;

import com.example.partnerservice.model.enums.AgreementStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "agreements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agreement {

    @Id
    @UuidGenerator
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Partner partner;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "document_path", nullable = false, length = 512)
    private String documentPath;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", columnDefinition = "agreement_status", nullable = false)
    @Builder.Default
    private AgreementStatus status = AgreementStatus.DRAFT;

    @Column(name = "signed_at")
    private OffsetDateTime signedAt;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "version", nullable = false)
    @Builder.Default
    private Integer version = 1;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
