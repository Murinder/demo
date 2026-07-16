package com.example.analyticsservice.controller;

import com.example.analyticsservice.dto.CreateReportDto;
import com.example.analyticsservice.dto.ReportDto;
import com.example.analyticsservice.service.ReportService;
import com.example.sharedlib.response.ApiResponse;
import com.example.sharedlib.security.AuthenticatedOnly;
import com.example.sharedlib.security.LecturerOrAbove;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Reports", description = "Report generation and management APIs")
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/generate")
    @LecturerOrAbove
    @Operation(summary = "Generate a new report")
    public ResponseEntity<ApiResponse<ReportDto>> generateReport(
            @RequestBody CreateReportDto dto,
            Authentication authentication) {
        UUID createdBy = UUID.fromString(authentication.getPrincipal().toString());
        ReportDto report = reportService.generateReport(dto, createdBy);
        return ResponseEntity.ok(ApiResponse.success(report, "Report generated"));
    }

    @GetMapping
    @Operation(summary = "Get all reports")
    public ResponseEntity<ApiResponse<List<ReportDto>>> getAllReports() {
        List<ReportDto> reports = reportService.getAllReports();
        return ResponseEntity.ok(ApiResponse.success(reports));
    }

    @GetMapping("/my")
    @Operation(summary = "Get reports created by current user")
    public ResponseEntity<ApiResponse<List<ReportDto>>> getMyReports(Authentication authentication) {
        UUID createdBy = UUID.fromString(authentication.getPrincipal().toString());
        List<ReportDto> reports = reportService.getReportsByCreator(createdBy);
        return ResponseEntity.ok(ApiResponse.success(reports));
    }

    @GetMapping("/{reportId}")
    @Operation(summary = "Get report by ID")
    public ResponseEntity<ApiResponse<ReportDto>> getReportById(@PathVariable UUID reportId) {
        ReportDto report = reportService.getReportById(reportId);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/{reportId}/download")
    @Operation(summary = "Download report file")
    public ResponseEntity<ApiResponse<String>> downloadReport(@PathVariable UUID reportId) {
        ReportDto report = reportService.getReportById(reportId);
        return ResponseEntity.ok(ApiResponse.success(report.getFilePath(), "Report file path"));
    }
}
