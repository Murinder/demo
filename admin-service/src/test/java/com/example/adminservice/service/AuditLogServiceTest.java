package com.example.adminservice.service;

import com.example.adminservice.dto.AuditLogDto;
import com.example.adminservice.model.entity.AuditLog;
import com.example.adminservice.model.enums.AuditAction;
import com.example.adminservice.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    @Test
    void record_shouldSaveAuditLog() {
        UUID userId = UUID.randomUUID();
        AuditLog saved = AuditLog.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .action(AuditAction.CREATE)
                .target("user")
                .ipAddress("127.0.0.1")
                .timestamp(OffsetDateTime.now())
                .build();

        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(saved);

        AuditLogDto result = auditLogService.record(userId, AuditAction.CREATE, "user",
                null, "127.0.0.1", "Mozilla/5.0", null, null);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getAction()).isEqualTo(AuditAction.CREATE);
        assertThat(result.getTarget()).isEqualTo("user");
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void query_withNoFilters_shouldReturnAll() {
        AuditLog log1 = AuditLog.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .action(AuditAction.LOGIN)
                .target("session")
                .ipAddress("10.0.0.1")
                .timestamp(OffsetDateTime.now())
                .build();

        when(auditLogRepository.findAllByOrderByTimestampDesc()).thenReturn(List.of(log1));

        List<AuditLogDto> results = auditLogService.query(null, null, null, null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getAction()).isEqualTo(AuditAction.LOGIN);
        verify(auditLogRepository).findAllByOrderByTimestampDesc();
    }

    @Test
    void query_withUserIdFilter_shouldFilterByUser() {
        UUID userId = UUID.randomUUID();
        OffsetDateTime from = OffsetDateTime.now().minusDays(1);
        OffsetDateTime to = OffsetDateTime.now();

        when(auditLogRepository.findByUserIdAndTimestampBetweenOrderByTimestampDesc(
                eq(userId), any(), any())).thenReturn(List.of());

        List<AuditLogDto> results = auditLogService.query(userId, null, from, to);

        assertThat(results).isEmpty();
        verify(auditLogRepository).findByUserIdAndTimestampBetweenOrderByTimestampDesc(
                eq(userId), any(), any());
    }

    @Test
    void query_withActionFilter_shouldFilterByAction() {
        OffsetDateTime from = OffsetDateTime.now().minusDays(7);
        OffsetDateTime to = OffsetDateTime.now();

        when(auditLogRepository.findByActionAndTimestampBetweenOrderByTimestampDesc(
                eq(AuditAction.DELETE), any(), any())).thenReturn(List.of());

        List<AuditLogDto> results = auditLogService.query(null, AuditAction.DELETE, from, to);

        assertThat(results).isEmpty();
        verify(auditLogRepository).findByActionAndTimestampBetweenOrderByTimestampDesc(
                eq(AuditAction.DELETE), any(), any());
    }

    @Test
    void query_withUserIdAndAction_shouldFilterByBoth() {
        UUID userId = UUID.randomUUID();
        OffsetDateTime from = OffsetDateTime.now().minusDays(3);
        OffsetDateTime to = OffsetDateTime.now();

        AuditLog log1 = AuditLog.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .action(AuditAction.UPDATE)
                .target("settings")
                .ipAddress("192.168.1.1")
                .timestamp(OffsetDateTime.now())
                .build();

        when(auditLogRepository.findByUserIdAndActionAndTimestampBetweenOrderByTimestampDesc(
                eq(userId), eq(AuditAction.UPDATE), any(), any())).thenReturn(List.of(log1));

        List<AuditLogDto> results = auditLogService.query(userId, AuditAction.UPDATE, from, to);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getAction()).isEqualTo(AuditAction.UPDATE);
        assertThat(results.get(0).getUserId()).isEqualTo(userId);
        verify(auditLogRepository).findByUserIdAndActionAndTimestampBetweenOrderByTimestampDesc(
                eq(userId), eq(AuditAction.UPDATE), any(), any());
    }

    @Test
    void record_shouldMapAllFieldsToDto() {
        UUID userId = UUID.randomUUID();
        Map<String, Object> details = Map.of("field", "value");
        Map<String, Object> before = Map.of("name", "old");
        Map<String, Object> after = Map.of("name", "new");

        AuditLog saved = AuditLog.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .action(AuditAction.UPDATE)
                .target("user")
                .details(details)
                .ipAddress("10.0.0.1")
                .userAgent("TestAgent/1.0")
                .timestamp(OffsetDateTime.now())
                .beforeState(before)
                .afterState(after)
                .build();

        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(saved);

        AuditLogDto result = auditLogService.record(userId, AuditAction.UPDATE, "user",
                details, "10.0.0.1", "TestAgent/1.0", before, after);

        assertThat(result.getIpAddress()).isEqualTo("10.0.0.1");
        assertThat(result.getUserAgent()).isEqualTo("TestAgent/1.0");
        assertThat(result.getDetails()).isEqualTo(details);
        assertThat(result.getBeforeState()).isEqualTo(before);
        assertThat(result.getAfterState()).isEqualTo(after);
    }

    @Test
    void query_withDateRangeOnly_shouldFilterByTimestamp() {
        OffsetDateTime from = OffsetDateTime.now().minusDays(30);
        OffsetDateTime to = OffsetDateTime.now();

        when(auditLogRepository.findByTimestampBetweenOrderByTimestampDesc(any(), any()))
                .thenReturn(List.of());

        List<AuditLogDto> results = auditLogService.query(null, null, from, to);

        assertThat(results).isEmpty();
        verify(auditLogRepository).findByTimestampBetweenOrderByTimestampDesc(any(), any());
    }
}
