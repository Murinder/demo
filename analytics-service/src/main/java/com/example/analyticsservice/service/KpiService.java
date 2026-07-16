package com.example.analyticsservice.service;

import com.example.analyticsservice.dto.KpiDto;
import com.example.analyticsservice.dto.KpiValueDto;
import com.example.analyticsservice.model.KpiMetric;
import com.example.analyticsservice.model.KpiValue;
import com.example.analyticsservice.repository.KpiMetricRepository;
import com.example.analyticsservice.repository.KpiValueRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class KpiService {

    private final KpiMetricRepository kpiMetricRepository;
    private final KpiValueRepository kpiValueRepository;

    public List<KpiValueDto> getKpiByProject(UUID projectId) {
        List<KpiValue> values = kpiValueRepository.findByEntityId(projectId);
        return values.stream().map(this::toValueDto).collect(Collectors.toList());
    }

    public List<KpiValueDto> getKpiByDepartment(UUID departmentId) {
        List<KpiValue> values = kpiValueRepository.findByEntityId(departmentId);
        return values.stream().map(this::toValueDto).collect(Collectors.toList());
    }

    public List<KpiDto> getPlatformKpis() {
        return kpiMetricRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public KpiDto createCustomKpi(KpiDto dto, UUID createdBy) {
        KpiMetric kpi = KpiMetric.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .calculationMethod(dto.getCalculationMethod() != null
                        ? KpiMetric.CalculationMethod.valueOf(dto.getCalculationMethod())
                        : KpiMetric.CalculationMethod.CUSTOM)
                .isCustom(true)
                .createdBy(createdBy)
                .formula(dto.getFormula())
                .targetValue(dto.getTargetValue())
                .build();

        kpi = kpiMetricRepository.save(kpi);
        return toDto(kpi);
    }

    @Transactional
    public KpiValueDto calculateKpiValue(UUID kpiId, UUID entityId) {
        KpiMetric kpi = kpiMetricRepository.findById(kpiId)
                .orElseThrow(() -> new ResourceNotFoundException("KPI", "id", kpiId));

        // Placeholder calculation - real logic would depend on calculation method
        BigDecimal calculatedValue = BigDecimal.ZERO;
        log.info("Calculated KPI '{}' for entity {}: {}", kpi.getName(), entityId, calculatedValue);

        KpiValue kpiValue = KpiValue.builder()
                .kpiId(kpiId)
                .value(calculatedValue)
                .period(LocalDate.now())
                .entityId(entityId)
                .createdAt(OffsetDateTime.now())
                .build();

        kpiValue = kpiValueRepository.save(kpiValue);
        return toValueDto(kpiValue);
    }

    public KpiDto getKpiById(UUID kpiId) {
        KpiMetric kpi = kpiMetricRepository.findById(kpiId)
                .orElseThrow(() -> new ResourceNotFoundException("KPI", "id", kpiId));
        return toDto(kpi);
    }

    private KpiDto toDto(KpiMetric k) {
        return KpiDto.builder()
                .id(k.getId())
                .name(k.getName())
                .description(k.getDescription())
                .calculationMethod(k.getCalculationMethod() != null ? k.getCalculationMethod().name() : null)
                .isCustom(k.getIsCustom())
                .createdBy(k.getCreatedBy())
                .createdAt(k.getCreatedAt())
                .updatedAt(k.getUpdatedAt())
                .formula(k.getFormula())
                .targetValue(k.getTargetValue())
                .build();
    }

    private KpiValueDto toValueDto(KpiValue v) {
        return KpiValueDto.builder()
                .kpiId(v.getKpiId())
                .kpiName(v.getKpi() != null ? v.getKpi().getName() : null)
                .value(v.getValue())
                .period(v.getPeriod())
                .entityId(v.getEntityId())
                .createdAt(v.getCreatedAt())
                .build();
    }
}
