package com.example.coreservice.model.entity;

import com.example.coreservice.model.enums.RelationshipType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * EmergencyContact entity - экстренные контакты пользователя
 */
@Entity
@Table(name = "emergency_contacts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyContact {
    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "contact_name", nullable = false)
    private String contactName;

    @Column(name = "contact_phone", nullable = false)
    private String contactPhone;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "relationship", nullable = false, columnDefinition = "relationship_type")
    @Enumerated(EnumType.STRING)
    private RelationshipType relationship;
}