package com.example.partnerservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "partner_contacts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerContact {

    @Id
    @UuidGenerator
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Partner partner;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "position", length = 100)
    private String position;

    @Column(name = "email")
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "is_primary")
    @Builder.Default
    private Boolean isPrimary = false;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
