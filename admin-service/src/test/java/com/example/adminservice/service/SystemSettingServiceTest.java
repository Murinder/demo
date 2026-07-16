package com.example.adminservice.service;

import com.example.adminservice.dto.SystemSettingDto;
import com.example.adminservice.model.entity.SystemSetting;
import com.example.adminservice.repository.SystemSettingRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemSettingServiceTest {

    @Mock
    private SystemSettingRepository systemSettingRepository;

    @InjectMocks
    private SystemSettingService systemSettingService;

    @Test
    void getAllSettings_shouldReturnAllSettings() {
        SystemSetting setting = SystemSetting.builder()
                .key("app.name")
                .value("ETSOPY")
                .description("Application name")
                .updatedAt(OffsetDateTime.now())
                .build();

        when(systemSettingRepository.findAll()).thenReturn(List.of(setting));

        List<SystemSettingDto> results = systemSettingService.getAllSettings();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getKey()).isEqualTo("app.name");
        assertThat(results.get(0).getValue()).isEqualTo("ETSOPY");
    }

    @Test
    void getSetting_whenExists_shouldReturn() {
        SystemSetting setting = SystemSetting.builder()
                .key("max.users")
                .value("1000")
                .build();

        when(systemSettingRepository.findById("max.users")).thenReturn(Optional.of(setting));

        SystemSettingDto result = systemSettingService.getSetting("max.users");

        assertThat(result.getKey()).isEqualTo("max.users");
        assertThat(result.getValue()).isEqualTo("1000");
    }

    @Test
    void getSetting_whenNotExists_shouldThrow() {
        when(systemSettingRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> systemSettingService.getSetting("nonexistent"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateSetting_whenExists_shouldUpdate() {
        UUID updatedBy = UUID.randomUUID();
        SystemSetting existing = SystemSetting.builder()
                .key("app.name")
                .value("OLD")
                .build();

        SystemSetting saved = SystemSetting.builder()
                .key("app.name")
                .value("NEW")
                .updatedBy(updatedBy)
                .updatedAt(OffsetDateTime.now())
                .build();

        when(systemSettingRepository.findById("app.name")).thenReturn(Optional.of(existing));
        when(systemSettingRepository.save(any(SystemSetting.class))).thenReturn(saved);

        SystemSettingDto dto = SystemSettingDto.builder().value("NEW").build();
        SystemSettingDto result = systemSettingService.updateSetting("app.name", dto, updatedBy);

        assertThat(result.getValue()).isEqualTo("NEW");
        assertThat(result.getUpdatedBy()).isEqualTo(updatedBy);
        verify(systemSettingRepository).save(any(SystemSetting.class));
    }

    @Test
    void updateSetting_whenNotExists_shouldCreateNew() {
        UUID updatedBy = UUID.randomUUID();

        SystemSetting saved = SystemSetting.builder()
                .key("new.key")
                .value("value")
                .updatedBy(updatedBy)
                .updatedAt(OffsetDateTime.now())
                .build();

        when(systemSettingRepository.findById("new.key")).thenReturn(Optional.empty());
        when(systemSettingRepository.save(any(SystemSetting.class))).thenReturn(saved);

        SystemSettingDto dto = SystemSettingDto.builder().value("value").build();
        SystemSettingDto result = systemSettingService.updateSetting("new.key", dto, updatedBy);

        assertThat(result.getKey()).isEqualTo("new.key");
        assertThat(result.getValue()).isEqualTo("value");
    }

    @Test
    void updateSetting_shouldUpdateDescription() {
        UUID updatedBy = UUID.randomUUID();
        SystemSetting existing = SystemSetting.builder()
                .key("app.mode")
                .value("production")
                .description("Old description")
                .build();

        SystemSetting saved = SystemSetting.builder()
                .key("app.mode")
                .value("staging")
                .description("Updated description")
                .updatedBy(updatedBy)
                .updatedAt(OffsetDateTime.now())
                .build();

        when(systemSettingRepository.findById("app.mode")).thenReturn(Optional.of(existing));
        when(systemSettingRepository.save(any(SystemSetting.class))).thenReturn(saved);

        SystemSettingDto dto = SystemSettingDto.builder()
                .value("staging")
                .description("Updated description")
                .build();
        SystemSettingDto result = systemSettingService.updateSetting("app.mode", dto, updatedBy);

        assertThat(result.getValue()).isEqualTo("staging");
        assertThat(result.getDescription()).isEqualTo("Updated description");
        verify(systemSettingRepository).save(any(SystemSetting.class));
    }

    @Test
    void getAllSettings_whenEmpty_shouldReturnEmptyList() {
        when(systemSettingRepository.findAll()).thenReturn(List.of());

        List<SystemSettingDto> results = systemSettingService.getAllSettings();

        assertThat(results).isEmpty();
        verify(systemSettingRepository).findAll();
    }

    @Test
    void getAllSettings_shouldReturnMultipleSettings() {
        SystemSetting s1 = SystemSetting.builder()
                .key("key1")
                .value("val1")
                .build();
        SystemSetting s2 = SystemSetting.builder()
                .key("key2")
                .value("val2")
                .build();

        when(systemSettingRepository.findAll()).thenReturn(List.of(s1, s2));

        List<SystemSettingDto> results = systemSettingService.getAllSettings();

        assertThat(results).hasSize(2);
        assertThat(results).extracting(SystemSettingDto::getKey)
                .containsExactlyInAnyOrder("key1", "key2");
    }
}
