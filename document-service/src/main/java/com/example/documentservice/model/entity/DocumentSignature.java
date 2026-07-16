package com.example.documentservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "document_signatures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentSignature {

    @EmbeddedId
    private DocumentSignatureId id;

    @Column(name = "signed_at")
    private OffsetDateTime signedAt;

    @Column(name = "signature_type", nullable = false, length = 50)
    private String signatureType;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", insertable = false, updatable = false)
    private GeneratedDocument document;

    @PrePersist
    protected void onCreate() {
        if (this.signedAt == null) {
            this.signedAt = OffsetDateTime.now();
        }
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Builder
    public static class DocumentSignatureId implements Serializable {

        @Column(name = "document_id")
        private UUID documentId;

        @Column(name = "signer_id")
        private UUID signerId;
    }
}
