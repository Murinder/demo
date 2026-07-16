package com.example.adminservice.service;

import com.example.adminservice.dto.SystemSettingDto;
import com.example.adminservice.model.entity.SystemSetting;
import com.example.adminservice.repository.SystemSettingRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemSettingService {

    private final SystemSettingRepository systemSettingRepository;

    public List<SystemSettingDto> getAllSettings() {
        return systemSettingRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public SystemSettingDto getSetting(String key) {
        SystemSetting setting = systemSettingRepository.findById(key)
                .orElseThrow(() -> new ResourceNotFoundException("SystemSetting", "key", key));
        return toDto(setting);
    }

    @Transactional
    public SystemSettingDto updateSetting(String key, SystemSettingDto dto, UUID updatedBy) {
        SystemSetting setting = systemSettingRepository.findById(key)
                .orElse(SystemSetting.builder().key(key).build());

        setting.setValue(dto.getValue());
        if (dto.getDescription() != null) {
            setting.setDescription(dto.getDescription());
        }
        setting.setUpdatedAt(OffsetDateTime.now());
        setting.setUpdatedBy(updatedBy);

        SystemSetting saved = systemSettingRepository.save(setting);
        log.info("Updated system setting: key={}", key);
        return toDto(saved);
    }

    private SystemSettingDto toDto(SystemSetting entity) {
        return SystemSettingDto.builder()
                .key(entity.getKey())
                .value(entity.getValue())
                .description(entity.getDescription())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
