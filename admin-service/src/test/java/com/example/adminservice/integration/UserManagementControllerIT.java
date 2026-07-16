package com.example.adminservice.integration;

import com.example.adminservice.dto.ChangeRoleRequest;
import com.example.adminservice.model.entity.UserRoleEntity;
import com.example.adminservice.model.enums.SystemRole;
import com.example.adminservice.repository.UserRoleRepository;
import com.example.testsupport.BaseIntegrationTest;
import com.example.testsupport.security.TestSecurityHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserManagementControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRoleRepository userRoleRepository;

    private final UUID adminId = UUID.randomUUID();
    private final UUID studentId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        userRoleRepository.deleteAll();
    }

    @Test
    void getAllUsers_AsAdmin_Returns200() throws Exception {
        userRoleRepository.save(UserRoleEntity.builder()
                .id(new UserRoleEntity.UserRolePK(UUID.randomUUID(), SystemRole.STUDENT))
                .assignedAt(OffsetDateTime.now())
                .build());

        mockMvc.perform(TestSecurityHelper.withAdmin(
                        get("/api/v1/admin/users"), adminId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getAllUsers_AsStudent_Returns403() throws Exception {
        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/admin/users"), studentId))
                .andExpect(status().isForbidden());
    }

    @Test
    void changeUserRole_AsAdmin_Returns200() throws Exception {
        UUID targetUserId = UUID.randomUUID();
        userRoleRepository.save(UserRoleEntity.builder()
                .id(new UserRoleEntity.UserRolePK(targetUserId, SystemRole.STUDENT))
                .assignedAt(OffsetDateTime.now())
                .build());

        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setRole(SystemRole.LECTURER);

        mockMvc.perform(TestSecurityHelper.withAdmin(
                        put("/api/v1/admin/users/" + targetUserId + "/role")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)),
                        adminId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void changeUserRole_AsStudent_Returns403() throws Exception {
        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setRole(SystemRole.ADMIN);

        mockMvc.perform(TestSecurityHelper.withStudent(
                        put("/api/v1/admin/users/" + UUID.randomUUID() + "/role")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)),
                        studentId))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_WithoutAuth_Returns403() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isForbidden());
    }
}
