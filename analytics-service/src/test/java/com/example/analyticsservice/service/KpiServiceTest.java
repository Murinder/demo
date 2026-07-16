package com.example.analyticsservice.service;

import com.example.analyticsservice.dto.KpiDto;
import com.example.analyticsservice.dto.KpiValueDto;
import com.example.analyticsservice.model.KpiMetric;
import com.example.analyticsservice.model.KpiValue;
import com.example.analyticsservice.repository.KpiMetricRepository;
import com.example.analyticsservice.repository.KpiValueRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiServiceTest {

    @Mock
    private KpiMetricRepository kpiMetricRepository;

    @Mock
    private KpiValueRepository kpiValueRepository;

    @InjectMocks
    private KpiService kpiService;

    @Test
    void getPlatformKpis_ReturnsAllMetrics() {
        KpiMetric kpi1 = KpiMetric.builder()
                .id(UUID.randomUUID())
                .name("Project Completion Rate")
                .description("Percentage of completed projects")
                .calculationMethod(KpiMetric.CalculationMethod.PROJECT_COMPLETION)
                .isCustom(false)
                .build();
        KpiMetric kpi2 = KpiMetric.builder()
                .id(UUID.randomUUID())
                .name("Student Engagement")
                .description("Student activity score")
                .calculationMethod(KpiMetric.CalculationMethod.STUDENT_ENGAGEMENT)
                .isCustom(false)
                .build();

        when(kpiMetricRepository.findAll()).thenReturn(List.of(kpi1, kpi2));

        List<KpiDto> result = kpiService.getPlatformKpis();

        assertEquals(2, result.size());
        assertEquals("Project Completion Rate", result.get(0).getName());
        assertEquals("Student Engagement", result.get(1).getName());
    }

    @Test
    void getPlatformKpis_Empty_ReturnsEmptyList() {
        when(kpiMetricRepository.findAll()).thenReturn(Collections.emptyList());

        List<KpiDto> result = kpiService.getPlatformKpis();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void createCustomKpi_ValidData_SavesAndReturnsDto() {
        UUID createdBy = UUID.randomUUID();
        KpiDto dto = KpiDto.builder()
                .name("Custom Metric")
                .description("A custom KPI for department X")
                .calculationMethod("CUSTOM")
                .formula("count(projects) / count(students)")
                .targetValue(new BigDecimal("85.00"))
                .build();

        when(kpiMetricRepository.save(any(KpiMetric.class))).thenAnswer(inv -> {
            KpiMetric k = inv.getArgument(0);
            k.setId(UUID.randomUUID());
            return k;
        });

        KpiDto result = kpiService.createCustomKpi(dto, createdBy);

        assertNotNull(result);
        assertEquals("Custom Metric", result.getName());
        assertEquals("A custom KPI for department X", result.getDescription());
        assertTrue(result.getIsCustom());
        assertEquals(createdBy, result.getCreatedBy());
        assertEquals("CUSTOM", result.getCalculationMethod());
        assertEquals("count(projects) / count(students)", result.getFormula());
        verify(kpiMetricRepository).save(any(KpiMetric.class));
    }

    @Test
    void createCustomKpi_NullCalculationMethod_DefaultsToCustom() {
        UUID createdBy = UUID.randomUUID();
        KpiDto dto = KpiDto.builder()
                .name("Simple Metric")
                .description("No method specified")
                .build();

        when(kpiMetricRepository.save(any(KpiMetric.class))).thenAnswer(inv -> {
            KpiMetric k = inv.getArgument(0);
            k.setId(UUID.randomUUID());
            return k;
        });

        KpiDto result = kpiService.createCustomKpi(dto, createdBy);

        assertNotNull(result);
        assertEquals("CUSTOM", result.getCalculationMethod());
    }

    @Test
    void getKpiById_Found_ReturnsDto() {
        UUID kpiId = UUID.randomUUID();
        KpiMetric kpi = KpiMetric.builder()
                .id(kpiId)
                .name("Event Success Rate")
                .description("Measures event completion")
                .calculationMethod(KpiMetric.CalculationMethod.EVENT_SUCCESS)
                .isCustom(false)
                .targetValue(new BigDecimal("90.00"))
                .build();

        when(kpiMetricRepository.findById(kpiId)).thenReturn(Optional.of(kpi));

        KpiDto result = kpiService.getKpiById(kpiId);

        assertNotNull(result);
        assertEquals(kpiId, result.getId());
        assertEquals("Event Success Rate", result.getName());
        assertEquals("EVENT_SUCCESS", result.getCalculationMethod());
    }

    @Test
    void getKpiById_NotFound_ThrowsException() {
        UUID kpiId = UUID.randomUUID();
        when(kpiMetricRepository.findById(kpiId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> kpiService.getKpiById(kpiId));
    }

    @Test
    void calculateKpiValue_ValidKpi_ReturnsValueDto() {
        UUID kpiId = UUID.randomUUID();
        UUID entityId = UUID.randomUUID();
        KpiMetric kpi = KpiMetric.builder()
                .id(kpiId)
                .name("Department Activity")
                .calculationMethod(KpiMetric.CalculationMethod.DEPARTMENT_ACTIVITY)
                .build();

        when(kpiMetricRepository.findById(kpiId)).thenReturn(Optional.of(kpi));
        when(kpiValueRepository.save(any(KpiValue.class))).thenAnswer(inv -> inv.getArgument(0));

        KpiValueDto result = kpiService.calculateKpiValue(kpiId, entityId);

        assertNotNull(result);
        assertEquals(kpiId, result.getKpiId());
        assertEquals(entityId, result.getEntityId());
        assertEquals(BigDecimal.ZERO, result.getValue());
        assertEquals(LocalDate.now(), result.getPeriod());
        verify(kpiValueRepository).save(any(KpiValue.class));
    }

    @Test
    void calculateKpiValue_KpiNotFound_ThrowsException() {
        UUID kpiId = UUID.randomUUID();
        UUID entityId = UUID.randomUUID();
        when(kpiMetricRepository.findById(kpiId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> kpiService.calculateKpiValue(kpiId, entityId));
        verify(kpiValueRepository, never()).save(any());
    }

    @Test
    void getKpiByProject_ReturnsValues() {
        UUID projectId = UUID.randomUUID();
        UUID kpiId = UUID.randomUUID();
        KpiValue value = KpiValue.builder()
                .kpiId(kpiId)
                .entityId(projectId)
                .value(new BigDecimal("75.50"))
                .period(LocalDate.of(2026, 3, 1))
                .createdAt(OffsetDateTime.now())
                .build();

        when(kpiValueRepository.findByEntityId(projectId)).thenReturn(List.of(value));

        List<KpiValueDto> result = kpiService.getKpiByProject(projectId);

        assertEquals(1, result.size());
        assertEquals(new BigDecimal("75.50"), result.get(0).getValue());
        assertEquals(projectId, result.get(0).getEntityId());
    }

    @Test
    void getKpiByDepartment_NoValues_ReturnsEmptyList() {
        UUID departmentId = UUID.randomUUID();
        when(kpiValueRepository.findByEntityId(departmentId)).thenReturn(Collections.emptyList());

        List<KpiValueDto> result = kpiService.getKpiByDepartment(departmentId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
