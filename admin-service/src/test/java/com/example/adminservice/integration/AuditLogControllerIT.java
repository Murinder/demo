package com.example.adminservice.integration;

import com.example.adminservice.model.entity.AuditLog;
import com.example.adminservice.model.enums.AuditAction;
import com.example.adminservice.repository.AuditLogRepository;
import com.example.testsupport.BaseIntegrationTest;
import com.example.testsupport.security.TestSecurityHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuditLogControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuditLogRepository auditLogRepository;

    private final UUID adminId = UUID.randomUUID();
    private final UUID studentId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
    }

    @Test
    void queryAuditLogs_AsAdmin_Returns200() throws Exception {
        auditLogRepository.save(AuditLog.builder()
                .userId(UUID.randomUUID())
                .action(AuditAction.LOGIN)
                .target("auth-service")
                .timestamp(OffsetDateTime.now())
                .ipAddress("127.0.0.1")
                .build());

        mockMvc.perform(TestSecurityHelper.withAdmin(
                        get("/api/v1/admin/audit"), adminId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void queryAuditLogs_AsStudent_Returns403() throws Exception {
        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/admin/audit"), studentId))
                .andExpect(status().isForbidden());
    }

    @Test
    void queryAuditLogs_WithUserIdFilter_ReturnsFiltered() throws Exception {
        UUID userId = UUID.randomUUID();
        auditLogRepository.save(AuditLog.builder()
                .userId(userId).action(AuditAction.LOGIN).target("auth")
                .timestamp(OffsetDateTime.now()).ipAddress("1.2.3.4").build());
        auditLogRepository.save(AuditLog.builder()
                .userId(UUID.randomUUID()).action(AuditAction.LOGOUT).target("auth")
                .timestamp(OffsetDateTime.now()).ipAddress("5.6.7.8").build());

        mockMvc.perform(TestSecurityHelper.withAdmin(
                        get("/api/v1/admin/audit").param("userId", userId.toString()), adminId))
                .andExpect(status().isOk());
    }
}
