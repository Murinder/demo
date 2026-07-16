package com.example.coreservice.service;

import com.example.coreservice.dto.DashboardDto;
import com.example.coreservice.model.entity.Dashboard;
import com.example.coreservice.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    public List<DashboardDto> getAllDashboards() {
        return dashboardRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public DashboardDto getDashboardByUserId(UUID userId) {
        return dashboardRepository.findById(userId)
                .map(this::mapToDto)
                .orElse(null); // Or throw an exception
    }

    public DashboardDto createDashboard(DashboardDto dashboardDto) {
        Dashboard dashboard = mapToEntity(dashboardDto);
        return mapToDto(dashboardRepository.save(dashboard));
    }

    public DashboardDto updateDashboard(UUID userId, DashboardDto dashboardDto) {
        if (!dashboardRepository.existsById(userId)) {
            return null; // Or throw an exception
        }
        Dashboard dashboard = mapToEntity(dashboardDto);
        dashboard.setUserId(userId);
        return mapToDto(dashboardRepository.save(dashboard));
    }

    public void deleteDashboard(UUID userId) {
        dashboardRepository.deleteById(userId);
    }

    private DashboardDto mapToDto(Dashboard dashboard) {
        return DashboardDto.builder()
                .userId(dashboard.getUserId())
                .widgetConfig(dashboard.getWidgetConfig() != null ? dashboard.getWidgetConfig().toString() : null)
                .build();
    }

    private Dashboard mapToEntity(DashboardDto dashboardDto) {
        Dashboard dashboard = new Dashboard();
        dashboard.setUserId(dashboardDto.getUserId());
        // In a real application, you would parse the JSON string to a proper JSON object
        // For simplicity, we'll just store it as a string here.
        // dashboard.setWidgetConfig(new JsonObject(dashboardDto.getWidgetConfig()));
        return dashboard;
    }
}