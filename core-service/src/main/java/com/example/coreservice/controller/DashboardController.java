package com.example.coreservice.controller;

import com.example.coreservice.dto.DashboardDto;
import com.example.coreservice.dto.DashboardSummaryDto;
import com.example.coreservice.service.DashboardAggregationService;
import com.example.coreservice.service.DashboardService;
import com.example.sharedlib.response.ApiResponse;
import com.example.sharedlib.security.AdminOnly;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboards")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Dashboard", description = "Dashboard management APIs")
public class DashboardController {
    private final DashboardService dashboardService;
    private final DashboardAggregationService dashboardAggregationService;

    @GetMapping("/{userId}/summary")
    @Operation(summary = "Get aggregated dashboard summary", description = "Returns role-specific KPIs and stats from multiple services")
    public ResponseEntity<ApiResponse<DashboardSummaryDto>> getSummary(@PathVariable UUID userId) {
        DashboardSummaryDto summary = dashboardAggregationService.getDashboardSummary(userId);
        return ResponseEntity.ok(ApiResponse.<DashboardSummaryDto>builder()
                .success(true)
                .code("DASHBOARD_SUMMARY_RETRIEVED")
                .message("Dashboard summary retrieved successfully")
                .data(summary)
                .build());
    }

    @AdminOnly
    @GetMapping
    @Operation(summary = "Get all dashboards")
    public ResponseEntity<List<DashboardDto>> getAll() {
        return ResponseEntity.ok(dashboardService.getAllDashboards());
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get dashboard by user id")
    public ResponseEntity<DashboardDto> getByUserId(@PathVariable UUID userId) {
        DashboardDto dashboardDto = dashboardService.getDashboardByUserId(userId);
        if (dashboardDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dashboardDto);
    }

    @PostMapping
    @Operation(summary = "Create a new dashboard")
    public ResponseEntity<DashboardDto> create(@RequestBody DashboardDto dashboardDto) {
        return ResponseEntity.ok(dashboardService.createDashboard(dashboardDto));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update a dashboard")
    public ResponseEntity<DashboardDto> update(@PathVariable UUID userId, @RequestBody DashboardDto dashboardDto) {
        DashboardDto updatedDashboard = dashboardService.updateDashboard(userId, dashboardDto);
        if (updatedDashboard == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedDashboard);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete a dashboard")
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        dashboardService.deleteDashboard(userId);
        return ResponseEntity.noContent().build();
    }
}