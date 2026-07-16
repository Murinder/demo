package com.example.adminservice.security;

import com.example.testsupport.BaseIntegrationTest;
import com.example.testsupport.security.TestSecurityHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AdminSecurityRbacIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminUsers_WithoutAuth_Returns403() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminUsers_AsStudent_Returns403() throws Exception {
        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/admin/users"), UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminUsers_AsLecturer_Returns403() throws Exception {
        mockMvc.perform(TestSecurityHelper.withLecturer(
                        get("/api/v1/admin/users"), UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminUsers_AsPartner_Returns403() throws Exception {
        mockMvc.perform(TestSecurityHelper.withPartner(
                        get("/api/v1/admin/users"), UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminUsers_AsDepartmentHead_Returns403() throws Exception {
        mockMvc.perform(TestSecurityHelper.withDepartmentHead(
                        get("/api/v1/admin/users"), UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminUsers_AsAdmin_Returns200() throws Exception {
        mockMvc.perform(TestSecurityHelper.withAdmin(
                        get("/api/v1/admin/users"), UUID.randomUUID()))
                .andExpect(status().isOk());
    }

    @Test
    void adminAudit_AsStudent_Returns403() throws Exception {
        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/admin/audit"), UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminSettings_AsStudent_Returns403() throws Exception {
        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/admin/settings"), UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminData_AsStudent_Returns403() throws Exception {
        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/admin/data/operations"), UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }
}
