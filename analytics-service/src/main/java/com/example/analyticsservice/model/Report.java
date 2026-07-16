package com.example.analyticsservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "report_type", nullable = false, length = 100)
    private String reportType;

    @Column(columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> parameters;

    @Column(name = "generated_at")
    private OffsetDateTime generatedAt;

    @Column(name = "file_path", nullable = false, length = 512)
    private String filePath;

    @Builder.Default
    @Column(name = "scheduled")
    private Boolean scheduled = false;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "format", nullable = false, columnDefinition = "report_format")
    @Builder.Default
    private ReportFormat format = ReportFormat.XLSX;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false, columnDefinition = "report_status")
    @Builder.Default
    private ReportStatus status = ReportStatus.PENDING;

    @Column(name = "error_message")
    private String errorMessage;

    public enum ReportFormat {
        XLSX, PDF, CSV
    }

    public enum ReportStatus {
        PENDING, PROCESSING, COMPLETED, FAILED
    }
}
