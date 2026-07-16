package com.example.documentservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "generated_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneratedDocument {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "template_id", nullable = false)
    private UUID templateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", insertable = false, updatable = false)
    private DocumentTemplate template;

    @Column(name = "generated_for", nullable = false)
    private UUID generatedFor;

    @Column(name = "file_path", nullable = false, length = 512)
    private String filePath;

    @Column(name = "generated_at")
    private OffsetDateTime generatedAt;

    @Column(name = "generated_by", nullable = false)
    private UUID generatedBy;

    @Column(name = "parameters", columnDefinition = "jsonb")
    private String parameters;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "signed_at")
    private OffsetDateTime signedAt;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        this.generatedAt = OffsetDateTime.now();
        if (this.status == null) {
            this.status = "COMPLETED";
        }
        if (this.parameters == null) {
            this.parameters = "{}";
        }
    }
}
