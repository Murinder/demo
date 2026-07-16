package com.example.adminservice.service;

import com.example.adminservice.dto.AuditLogDto;
import com.example.adminservice.model.entity.AuditLog;
import com.example.adminservice.model.enums.AuditAction;
import com.example.adminservice.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public AuditLogDto record(UUID userId, AuditAction action, String target,
                              Map<String, Object> details, String ipAddress, String userAgent,
                              Map<String, Object> beforeState, Map<String, Object> afterState) {
        AuditLog entity = AuditLog.builder()
                .userId(userId)
                .action(action)
                .target(target)
                .details(details)
                .timestamp(OffsetDateTime.now())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .beforeState(beforeState)
                .afterState(afterState)
                .build();

        AuditLog saved = auditLogRepository.save(entity);
        log.debug("Recorded audit log: user={}, action={}, target={}", userId, action, target);
        return toDto(saved);
    }

    public List<AuditLogDto> query(UUID userId, AuditAction action,
                                   OffsetDateTime dateFrom, OffsetDateTime dateTo) {
        // Default date range if not provided
        OffsetDateTime from = dateFrom != null ? dateFrom : OffsetDateTime.now().minusYears(1);
        OffsetDateTime to = dateTo != null ? dateTo : OffsetDateTime.now();

        List<AuditLog> results;

        if (userId != null && action != null) {
            results = auditLogRepository.findByUserIdAndActionAndTimestampBetweenOrderByTimestampDesc(
                    userId, action, from, to);
        } else if (userId != null) {
            results = auditLogRepository.findByUserIdAndTimestampBetweenOrderByTimestampDesc(
                    userId, from, to);
        } else if (action != null) {
            results = auditLogRepository.findByActionAndTimestampBetweenOrderByTimestampDesc(
                    action, from, to);
        } else if (dateFrom != null || dateTo != null) {
            results = auditLogRepository.findByTimestampBetweenOrderByTimestampDesc(from, to);
        } else {
            results = auditLogRepository.findAllByOrderByTimestampDesc();
        }

        return results.stream().map(this::toDto).collect(Collectors.toList());
    }

    private AuditLogDto toDto(AuditLog entity) {
        return AuditLogDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .action(entity.getAction())
                .target(entity.getTarget())
                .details(entity.getDetails())
                .timestamp(entity.getTimestamp())
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .beforeState(entity.getBeforeState())
                .afterState(entity.getAfterState())
                .build();
    }
}
