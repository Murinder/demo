package com.example.adminservice.integration;

import com.example.adminservice.dto.SystemSettingDto;
import com.example.adminservice.model.entity.SystemSetting;
import com.example.adminservice.repository.SystemSettingRepository;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SystemSettingControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SystemSettingRepository settingRepository;

    private final UUID adminId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        settingRepository.deleteAll();
    }

    @Test
    void getAllSettings_AsAdmin_Returns200() throws Exception {
        settingRepository.save(SystemSetting.builder()
                .key("max_upload_size")
                .value("10MB")
                .description("Maximum file upload size")
                .build());

        mockMvc.perform(TestSecurityHelper.withAdmin(
                        get("/api/v1/admin/settings"), adminId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void updateSetting_AsAdmin_Returns200AndUpdatesDb() throws Exception {
        settingRepository.save(SystemSetting.builder()
                .key("maintenance_mode")
                .value("false")
                .description("Enable maintenance mode")
                .build());

        SystemSettingDto updateDto = SystemSettingDto.builder()
                .value("true")
                .description("Enable maintenance mode")
                .build();

        mockMvc.perform(TestSecurityHelper.withAdmin(
                        put("/api/v1/admin/settings/maintenance_mode")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDto)),
                        adminId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        SystemSetting updated = settingRepository.findById("maintenance_mode").orElseThrow();
        assertThat(updated.getValue()).isEqualTo("true");
    }

    @Test
    void getAllSettings_AsStudent_Returns403() throws Exception {
        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/admin/settings"), UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }
}
