package com.example.coreservice.model.entity;

import com.example.coreservice.model.enums.ApplicationKind;
import com.example.coreservice.model.enums.ApplicationPriority;
import com.example.coreservice.model.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "lecturer_id", nullable = false)
    private UUID lecturerId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "application_status")
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "application_priority")
    @Builder.Default
    private ApplicationPriority priority = ApplicationPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "application_kind")
    private ApplicationKind kind;

    @Column
    private String category;

    @Column(length = 100)
    private String duration;

    @Column(name = "team_size", length = 100)
    private String teamSize;

    @Column(name = "teacher_reply", columnDefinition = "TEXT")
    private String teacherReply;

    @Column(name = "application_number", insertable = false, updatable = false)
    private Integer applicationNumber;

    @Column(name = "admin_id")
    private UUID adminId;

    @Column(name = "submitted_at", updatable = false)
    @Builder.Default
    private OffsetDateTime submittedAt = OffsetDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();
}
