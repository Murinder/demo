package com.example.analyticsservice.service;

import com.example.analyticsservice.dto.CreateReportDto;
import com.example.analyticsservice.dto.ReportDto;
import com.example.analyticsservice.model.Report;
import com.example.analyticsservice.repository.ReportRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportExportService reportExportService;

    @Transactional
    public ReportDto generateReport(CreateReportDto dto, UUID createdBy) {
        Report.ReportFormat format = dto.getFormat() != null
                ? Report.ReportFormat.valueOf(dto.getFormat())
                : Report.ReportFormat.XLSX;

        Report report = Report.builder()
                .reportType(dto.getReportType())
                .parameters(dto.getParameters() != null ? dto.getParameters() : Collections.emptyMap())
                .createdBy(createdBy)
                .format(format)
                .status(Report.ReportStatus.PENDING)
                .scheduled(dto.getScheduled() != null && dto.getScheduled())
                .filePath("")
                .build();

        report = reportRepository.save(report);

        try {
            report.setStatus(Report.ReportStatus.PROCESSING);
            report = reportRepository.save(report);

            String filePath = reportExportService.exportReport(report);
            report.setFilePath(filePath);
            report.setStatus(Report.ReportStatus.COMPLETED);
            report.setGeneratedAt(OffsetDateTime.now());
        } catch (Exception e) {
            log.error("Failed to generate report {}: {}", report.getId(), e.getMessage());
            report.setStatus(Report.ReportStatus.FAILED);
            report.setErrorMessage(e.getMessage());
        }

        report = reportRepository.save(report);
        return toDto(report);
    }

    public ReportDto getReportById(UUID reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report", "id", reportId));
        return toDto(report);
    }

    public List<ReportDto> getAllReports() {
        return reportRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ReportDto> getReportsByCreator(UUID createdBy) {
        return reportRepository.findByCreatedByOrderByGeneratedAtDesc(createdBy).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ReportDto toDto(Report r) {
        return ReportDto.builder()
                .id(r.getId())
                .reportType(r.getReportType())
                .parameters(r.getParameters())
                .generatedAt(r.getGeneratedAt())
                .filePath(r.getFilePath())
                .scheduled(r.getScheduled())
                .createdBy(r.getCreatedBy())
                .format(r.getFormat() != null ? r.getFormat().name() : null)
                .status(r.getStatus() != null ? r.getStatus().name() : null)
                .errorMessage(r.getErrorMessage())
                .build();
    }
}
