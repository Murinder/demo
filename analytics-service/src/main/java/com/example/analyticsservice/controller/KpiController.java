package com.example.analyticsservice.controller;

import com.example.analyticsservice.dto.KpiDto;
import com.example.analyticsservice.dto.KpiValueDto;
import com.example.analyticsservice.service.KpiService;
import com.example.sharedlib.response.ApiResponse;
import com.example.sharedlib.security.AdminOnly;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/kpi")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "KPI", description = "Key Performance Indicator APIs")
public class KpiController {

    private final KpiService kpiService;

    @GetMapping("/projects/{projectId}")
    @Operation(summary = "Get KPI values for a project")
    public ResponseEntity<ApiResponse<List<KpiValueDto>>> getProjectKpis(@PathVariable UUID projectId) {
        List<KpiValueDto> kpis = kpiService.getKpiByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(kpis));
    }

    @GetMapping("/departments/{departmentId}")
    @Operation(summary = "Get KPI values for a department")
    public ResponseEntity<ApiResponse<List<KpiValueDto>>> getDepartmentKpis(@PathVariable UUID departmentId) {
        List<KpiValueDto> kpis = kpiService.getKpiByDepartment(departmentId);
        return ResponseEntity.ok(ApiResponse.success(kpis));
    }

    @GetMapping("/platform")
    @Operation(summary = "Get all platform KPIs")
    public ResponseEntity<ApiResponse<List<KpiDto>>> getPlatformKpis() {
        List<KpiDto> kpis = kpiService.getPlatformKpis();
        return ResponseEntity.ok(ApiResponse.success(kpis));
    }

    @PostMapping("/custom")
    @AdminOnly
    @Operation(summary = "Create a custom KPI (admin only)")
    public ResponseEntity<ApiResponse<KpiDto>> createCustomKpi(
            @RequestBody KpiDto dto,
            Authentication authentication) {
        UUID createdBy = UUID.fromString(authentication.getPrincipal().toString());
        KpiDto kpi = kpiService.createCustomKpi(dto, createdBy);
        return ResponseEntity.ok(ApiResponse.success(kpi, "Custom KPI created"));
    }
}
