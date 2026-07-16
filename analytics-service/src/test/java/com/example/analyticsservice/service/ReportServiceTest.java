package com.example.analyticsservice.service;

import com.example.analyticsservice.dto.CreateReportDto;
import com.example.analyticsservice.dto.ReportDto;
import com.example.analyticsservice.model.Report;
import com.example.analyticsservice.repository.ReportRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReportExportService reportExportService;

    @InjectMocks
    private ReportService reportService;

    @Test
    void generateReport_ValidDto_ReturnsCompletedReport() {
        UUID createdBy = UUID.randomUUID();
        CreateReportDto dto = CreateReportDto.builder()
                .reportType("PROJECTS")
                .format("PDF")
                .parameters(Collections.singletonMap("key", "value"))
                .build();

        Report savedReport = Report.builder()
                .id(UUID.randomUUID())
                .reportType("PROJECTS")
                .parameters(dto.getParameters())
                .createdBy(createdBy)
                .format(Report.ReportFormat.PDF)
                .status(Report.ReportStatus.PENDING)
                .filePath("")
                .build();

        when(reportRepository.save(any(Report.class))).thenReturn(savedReport);
        when(reportExportService.exportReport(any(Report.class))).thenReturn("/reports/generated/projects_test.pdf");

        ReportDto result = reportService.generateReport(dto, createdBy);

        assertNotNull(result);
        assertEquals("PROJECTS", result.getReportType());
        assertEquals(createdBy, result.getCreatedBy());
        verify(reportRepository, atLeastOnce()).save(any(Report.class));
        verify(reportExportService).exportReport(any(Report.class));
    }

    @Test
    void getReportById_Exists_ReturnsReport() {
        UUID reportId = UUID.randomUUID();
        Report report = Report.builder()
                .id(reportId)
                .reportType("DEPARTMENTS")
                .parameters(Collections.emptyMap())
                .createdBy(UUID.randomUUID())
                .format(Report.ReportFormat.XLSX)
                .status(Report.ReportStatus.COMPLETED)
                .filePath("/reports/generated/departments_test.xlsx")
                .build();

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));

        ReportDto result = reportService.getReportById(reportId);

        assertNotNull(result);
        assertEquals(reportId, result.getId());
        assertEquals("DEPARTMENTS", result.getReportType());
        assertEquals("COMPLETED", result.getStatus());
    }

    @Test
    void getReportById_NotFound_ThrowsException() {
        UUID reportId = UUID.randomUUID();
        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reportService.getReportById(reportId));
    }

    @Test
    void generateReport_ExportFails_ReturnsFailedStatus() {
        UUID createdBy = UUID.randomUUID();
        CreateReportDto dto = CreateReportDto.builder()
                .reportType("USERS")
                .format("CSV")
                .build();

        Report savedReport = Report.builder()
                .id(UUID.randomUUID())
                .reportType("USERS")
                .parameters(Collections.emptyMap())
                .createdBy(createdBy)
                .format(Report.ReportFormat.CSV)
                .status(Report.ReportStatus.PENDING)
                .filePath("")
                .build();

        when(reportRepository.save(any(Report.class))).thenReturn(savedReport);
        when(reportExportService.exportReport(any(Report.class)))
                .thenThrow(new RuntimeException("Disk full"));

        ReportDto result = reportService.generateReport(dto, createdBy);

        assertNotNull(result);
        assertEquals("FAILED", result.getStatus());
    }

    @Test
    void getAllReports_ReturnsAllReports() {
        Report r1 = Report.builder()
                .id(UUID.randomUUID())
                .reportType("PROJECTS")
                .parameters(Collections.emptyMap())
                .createdBy(UUID.randomUUID())
                .format(Report.ReportFormat.PDF)
                .status(Report.ReportStatus.COMPLETED)
                .filePath("/reports/generated/projects_test.pdf")
                .build();
        Report r2 = Report.builder()
                .id(UUID.randomUUID())
                .reportType("EVENTS")
                .parameters(Collections.emptyMap())
                .createdBy(UUID.randomUUID())
                .format(Report.ReportFormat.XLSX)
                .status(Report.ReportStatus.COMPLETED)
                .filePath("/reports/generated/events_test.xlsx")
                .build();

        when(reportRepository.findAll()).thenReturn(List.of(r1, r2));

        List<ReportDto> result = reportService.getAllReports();

        assertEquals(2, result.size());
        assertEquals("PROJECTS", result.get(0).getReportType());
        assertEquals("EVENTS", result.get(1).getReportType());
    }

    @Test
    void getReportsByCreator_ReturnsFilteredReports() {
        UUID createdBy = UUID.randomUUID();
        Report report = Report.builder()
                .id(UUID.randomUUID())
                .reportType("DEPARTMENTS")
                .parameters(Collections.emptyMap())
                .createdBy(createdBy)
                .format(Report.ReportFormat.XLSX)
                .status(Report.ReportStatus.COMPLETED)
                .filePath("/reports/generated/departments_test.xlsx")
                .build();

        when(reportRepository.findByCreatedByOrderByGeneratedAtDesc(createdBy))
                .thenReturn(List.of(report));

        List<ReportDto> result = reportService.getReportsByCreator(createdBy);

        assertEquals(1, result.size());
        assertEquals(createdBy, result.get(0).getCreatedBy());
        assertEquals("DEPARTMENTS", result.get(0).getReportType());
    }

    @Test
    void generateReport_DefaultFormat_UsesXlsx() {
        UUID createdBy = UUID.randomUUID();
        CreateReportDto dto = CreateReportDto.builder()
                .reportType("CUSTOM")
                .build();

        Report savedReport = Report.builder()
                .id(UUID.randomUUID())
                .reportType("CUSTOM")
                .parameters(Collections.emptyMap())
                .createdBy(createdBy)
                .format(Report.ReportFormat.XLSX)
                .status(Report.ReportStatus.PENDING)
                .filePath("")
                .build();

        when(reportRepository.save(any(Report.class))).thenReturn(savedReport);
        when(reportExportService.exportReport(any(Report.class)))
                .thenReturn("/reports/generated/custom_test.xlsx");

        ReportDto result = reportService.generateReport(dto, createdBy);

        assertNotNull(result);
        assertEquals("XLSX", result.getFormat());
    }
}
