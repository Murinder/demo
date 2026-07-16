package com.example.coreservice.service;

import com.example.coreservice.dto.DashboardDto;
import com.example.coreservice.model.entity.Dashboard;
import com.example.coreservice.repository.DashboardRepository;
import org.junit.jupiter.api.BeforeEach;
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
class DashboardServiceTest {

    @Mock
    private DashboardRepository dashboardRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private UUID userId;
    private Dashboard testDashboard;
    private DashboardDto testDashboardDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testDashboard = Dashboard.builder()
                .userId(userId)
                .widgetConfig("{\"widgets\": []}")
                .build();
        testDashboardDto = DashboardDto.builder()
                .userId(userId)
                .widgetConfig("{\"widgets\": []}")
                .build();
    }

    @Test
    void getAllDashboards_ReturnsList() {
        when(dashboardRepository.findAll()).thenReturn(List.of(testDashboard));

        List<DashboardDto> result = dashboardService.getAllDashboards();

        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
        verify(dashboardRepository, times(1)).findAll();
    }

    @Test
    void getAllDashboards_ReturnsEmptyList() {
        when(dashboardRepository.findAll()).thenReturn(Collections.emptyList());

        List<DashboardDto> result = dashboardService.getAllDashboards();

        assertTrue(result.isEmpty());
    }

    @Test
    void getDashboardByUserId_WhenExists_ReturnsDashboard() {
        when(dashboardRepository.findById(userId)).thenReturn(Optional.of(testDashboard));

        DashboardDto result = dashboardService.getDashboardByUserId(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
    }

    @Test
    void getDashboardByUserId_WhenNotExists_ReturnsNull() {
        UUID id = UUID.randomUUID();
        when(dashboardRepository.findById(id)).thenReturn(Optional.empty());

        DashboardDto result = dashboardService.getDashboardByUserId(id);

        assertNull(result);
    }

    @Test
    void createDashboard_Success() {
        when(dashboardRepository.save(any(Dashboard.class))).thenAnswer(invocation -> {
            Dashboard d = invocation.getArgument(0);
            d.setUserId(userId);
            return d;
        });

        DashboardDto result = dashboardService.createDashboard(testDashboardDto);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(dashboardRepository, times(1)).save(any(Dashboard.class));
    }

    @Test
    void updateDashboard_WhenExists_ReturnsUpdated() {
        when(dashboardRepository.existsById(userId)).thenReturn(true);
        when(dashboardRepository.save(any(Dashboard.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DashboardDto result = dashboardService.updateDashboard(userId, testDashboardDto);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(dashboardRepository, times(1)).save(any(Dashboard.class));
    }

    @Test
    void updateDashboard_WhenNotExists_ReturnsNull() {
        UUID id = UUID.randomUUID();
        when(dashboardRepository.existsById(id)).thenReturn(false);

        DashboardDto result = dashboardService.updateDashboard(id, testDashboardDto);

        assertNull(result);
        verify(dashboardRepository, never()).save(any(Dashboard.class));
    }

    @Test
    void deleteDashboard_CallsRepository() {
        doNothing().when(dashboardRepository).deleteById(userId);

        dashboardService.deleteDashboard(userId);

        verify(dashboardRepository, times(1)).deleteById(userId);
    }
}
