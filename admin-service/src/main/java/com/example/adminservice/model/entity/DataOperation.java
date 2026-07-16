package com.example.adminservice.model.entity;

import com.example.adminservice.model.enums.OperationStatus;
import com.example.adminservice.model.enums.OperationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "data_operations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataOperation {

    @Id
    @UuidGenerator
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false)
    private OperationType operationType;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "file_path")
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OperationStatus status;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "record_count")
    private Integer recordCount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "details", columnDefinition = "jsonb")
    private Map<String, Object> details;
}
