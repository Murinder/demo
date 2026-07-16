package com.example.analyticsservice.integration;

import com.example.analyticsservice.dto.KpiDto;
import com.example.analyticsservice.model.KpiMetric;
import com.example.analyticsservice.model.KpiValue;
import com.example.analyticsservice.repository.KpiMetricRepository;
import com.example.analyticsservice.repository.KpiValueRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class KpiControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KpiMetricRepository kpiMetricRepository;

    @Autowired
    private KpiValueRepository kpiValueRepository;

    private static final UUID ADMIN_ID = UUID.randomUUID();
    private static final UUID STUDENT_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        kpiValueRepository.deleteAll();
        kpiMetricRepository.deleteAll();
    }

    @Test
    void getPlatformKpis_returnsAllKpis() throws Exception {
        createKpiMetricInDb("Student Engagement", KpiMetric.CalculationMethod.STUDENT_ENGAGEMENT);
        createKpiMetricInDb("Project Completion", KpiMetric.CalculationMethod.PROJECT_COMPLETION);

        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/kpi/platform"), STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void getProjectKpis_returnsKpiValuesForProject() throws Exception {
        UUID projectId = UUID.randomUUID();
        KpiMetric kpi = createKpiMetricInDb("Project KPI", KpiMetric.CalculationMethod.PROJECT_COMPLETION);
        createKpiValueInDb(kpi.getId(), projectId, BigDecimal.valueOf(85.5));

        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/kpi/projects/{projectId}", projectId), STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].value").value(85.5));
    }

    @Test
    void getDepartmentKpis_returnsKpiValuesForDepartment() throws Exception {
        UUID departmentId = UUID.randomUUID();
        KpiMetric kpi = createKpiMetricInDb("Dept KPI", KpiMetric.CalculationMethod.DEPARTMENT_ACTIVITY);
        createKpiValueInDb(kpi.getId(), departmentId, BigDecimal.valueOf(92.0));

        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/kpi/departments/{departmentId}", departmentId), STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void createCustomKpi_adminRole_succeeds() throws Exception {
        KpiDto dto = KpiDto.builder()
                .name("Custom Metric")
                .description("A custom KPI")
                .calculationMethod("CUSTOM")
                .formula("SUM(x)/COUNT(y)")
                .targetValue(BigDecimal.valueOf(100))
                .build();

        mockMvc.perform(TestSecurityHelper.withAdmin(
                        post("/api/v1/kpi/custom"), ADMIN_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Custom Metric"))
                .andExpect(jsonPath("$.data.isCustom").value(true));

        assertThat(kpiMetricRepository.findAll()).hasSize(1);
        KpiMetric saved = kpiMetricRepository.findAll().get(0);
        assertThat(saved.getIsCustom()).isTrue();
        assertThat(saved.getCreatedBy()).isEqualTo(ADMIN_ID);
    }

    @Test
    void createCustomKpi_studentRole_getsForbidden() throws Exception {
        KpiDto dto = KpiDto.builder()
                .name("Custom Metric")
                .calculationMethod("CUSTOM")
                .build();

        mockMvc.perform(TestSecurityHelper.withStudent(
                        post("/api/v1/kpi/custom"), STUDENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        assertThat(kpiMetricRepository.findAll()).isEmpty();
    }

    @Test
    void unauthenticated_returns403() throws Exception {
        mockMvc.perform(get("/api/v1/kpi/platform"))
                .andExpect(status().isForbidden());
    }

    private KpiMetric createKpiMetricInDb(String name, KpiMetric.CalculationMethod method) {
        return kpiMetricRepository.save(KpiMetric.builder()
                .name(name)
                .description("Test KPI: " + name)
                .calculationMethod(method)
                .isCustom(false)
                .createdBy(ADMIN_ID)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build());
    }

    private KpiValue createKpiValueInDb(UUID kpiId, UUID entityId, BigDecimal value) {
        return kpiValueRepository.save(KpiValue.builder()
                .kpiId(kpiId)
                .entityId(entityId)
                .value(value)
                .period(LocalDate.now())
                .createdAt(OffsetDateTime.now())
                .build());
    }
}
