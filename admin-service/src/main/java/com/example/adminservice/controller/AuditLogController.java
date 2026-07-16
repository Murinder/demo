package com.example.adminservice.controller;

import com.example.adminservice.dto.AuditLogDto;
import com.example.adminservice.model.enums.AuditAction;
import com.example.adminservice.service.AuditLogService;
import com.example.sharedlib.response.ApiResponse;
import com.example.sharedlib.security.AdminOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/audit")
@RequiredArgsConstructor
@AdminOnly
@Tag(name = "Audit Log", description = "Журнал аудита")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @Operation(summary = "Get audit logs with optional filters")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AuditLogDto>>> getAuditLogs(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) AuditAction action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateTo) {
        List<AuditLogDto> logs = auditLogService.query(userId, action, dateFrom, dateTo);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }
}
