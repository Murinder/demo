package com.example.analyticsservice.integration;

import com.example.analyticsservice.dto.CreateReportDto;
import com.example.analyticsservice.model.Report;
import com.example.analyticsservice.repository.ReportRepository;
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
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ReportControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReportRepository reportRepository;

    private static final UUID LECTURER_ID = UUID.randomUUID();
    private static final UUID STUDENT_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        reportRepository.deleteAll();
    }

    @Test
    void generateReport_lecturerRole_createsReport() throws Exception {
        CreateReportDto dto = CreateReportDto.builder()
                .reportType("PROJECTS")
                .parameters(Map.of("year", 2025))
                .format("PDF")
                .scheduled(false)
                .build();

        mockMvc.perform(TestSecurityHelper.withLecturer(
                        post("/api/v1/reports/generate"), LECTURER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.reportType").value("PROJECTS"))
                .andExpect(jsonPath("$.data.createdBy").value(LECTURER_ID.toString()));

        assertThat(reportRepository.findAll()).hasSize(1);
    }

    @Test
    void generateReport_studentRole_getsForbidden() throws Exception {
        CreateReportDto dto = CreateReportDto.builder()
                .reportType("PROJECTS")
                .parameters(Collections.emptyMap())
                .format("XLSX")
                .build();

        mockMvc.perform(TestSecurityHelper.withStudent(
                        post("/api/v1/reports/generate"), STUDENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        assertThat(reportRepository.findAll()).isEmpty();
    }

    @Test
    void getAllReports_returnsListOfReports() throws Exception {
        createReportInDb("PROJECTS", LECTURER_ID);
        createReportInDb("USERS", LECTURER_ID);

        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/reports"), STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void getReportById_happyPath_returnsReport() throws Exception {
        Report report = createReportInDb("DEPARTMENTS", LECTURER_ID);

        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/reports/{reportId}", report.getId()), STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(report.getId().toString()));
    }

    @Test
    void getReportById_notFound_returns404() throws Exception {
        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/reports/{reportId}", UUID.randomUUID()), STUDENT_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void downloadReport_happyPath_returnsFilePath() throws Exception {
        Report report = createReportInDb("EVENTS", LECTURER_ID);

        mockMvc.perform(TestSecurityHelper.withStudent(
                        get("/api/v1/reports/{reportId}/download", report.getId()), STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(report.getFilePath()));
    }

    @Test
    void unauthenticated_returns403() throws Exception {
        mockMvc.perform(get("/api/v1/reports"))
                .andExpect(status().isForbidden());
    }

    private Report createReportInDb(String type, UUID createdBy) {
        return reportRepository.save(Report.builder()
                .reportType(type)
                .parameters(Collections.emptyMap())
                .filePath("/reports/test_" + UUID.randomUUID().toString().substring(0, 8) + ".pdf")
                .createdBy(createdBy)
                .format(Report.ReportFormat.PDF)
                .status(Report.ReportStatus.COMPLETED)
                .generatedAt(OffsetDateTime.now())
                .build());
    }
}
