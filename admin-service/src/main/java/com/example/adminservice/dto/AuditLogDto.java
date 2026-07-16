package com.example.adminservice.dto;

import com.example.adminservice.model.enums.AuditAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogDto {
    private UUID id;
    private UUID userId;
    private AuditAction action;
    private String target;
    private Map<String, Object> details;
    private OffsetDateTime timestamp;
    private String ipAddress;
    private String userAgent;
    private Map<String, Object> beforeState;
    private Map<String, Object> afterState;
}
