package com.example.adminservice.repository;

import com.example.adminservice.model.entity.AuditLog;
import com.example.adminservice.model.enums.AuditAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findByUserIdOrderByTimestampDesc(UUID userId);

    List<AuditLog> findByActionOrderByTimestampDesc(AuditAction action);

    List<AuditLog> findByTimestampBetweenOrderByTimestampDesc(OffsetDateTime from, OffsetDateTime to);

    List<AuditLog> findByUserIdAndActionAndTimestampBetweenOrderByTimestampDesc(
            UUID userId, AuditAction action, OffsetDateTime from, OffsetDateTime to);

    List<AuditLog> findByUserIdAndTimestampBetweenOrderByTimestampDesc(
            UUID userId, OffsetDateTime from, OffsetDateTime to);

    List<AuditLog> findByActionAndTimestampBetweenOrderByTimestampDesc(
            AuditAction action, OffsetDateTime from, OffsetDateTime to);

    List<AuditLog> findAllByOrderByTimestampDesc();
}
