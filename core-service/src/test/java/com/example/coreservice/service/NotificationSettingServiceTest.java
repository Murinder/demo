package com.example.coreservice.service;

import com.example.coreservice.dto.NotificationSettingDto;
import com.example.coreservice.model.entity.NotificationSetting;
import com.example.coreservice.repository.NotificationSettingRepository;
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
class NotificationSettingServiceTest {

    @Mock
    private NotificationSettingRepository notificationSettingRepository;

    @InjectMocks
    private NotificationSettingService notificationSettingService;

    @Test
    void getAllNotificationSettings() {
        when(notificationSettingRepository.findAll()).thenReturn(Collections.singletonList(new NotificationSetting()));

        List<NotificationSettingDto> result = notificationSettingService.getAllNotificationSettings();

        assertEquals(1, result.size());
        verify(notificationSettingRepository, times(1)).findAll();
    }

    @Test
    void getNotificationSettingByUserId_whenExists() {
        UUID userId = UUID.randomUUID();
        NotificationSetting setting = new NotificationSetting();
        setting.setUserId(userId);
        when(notificationSettingRepository.findById(userId)).thenReturn(Optional.of(setting));

        NotificationSettingDto result = notificationSettingService.getNotificationSettingByUserId(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(notificationSettingRepository, times(1)).findById(userId);
    }

    @Test
    void getNotificationSettingByUserId_whenNotExists() {
        UUID userId = UUID.randomUUID();
        when(notificationSettingRepository.findById(userId)).thenReturn(Optional.empty());

        NotificationSettingDto result = notificationSettingService.getNotificationSettingByUserId(userId);

        assertNull(result);
        verify(notificationSettingRepository, times(1)).findById(userId);
    }

    @Test
    void createNotificationSetting() {
        NotificationSettingDto dto = new NotificationSettingDto();
        dto.setUserId(UUID.randomUUID());
        NotificationSetting setting = new NotificationSetting();
        setting.setUserId(dto.getUserId());

        when(notificationSettingRepository.save(any(NotificationSetting.class))).thenReturn(setting);

        NotificationSettingDto result = notificationSettingService.createNotificationSetting(dto);

        assertNotNull(result);
        assertEquals(dto.getUserId(), result.getUserId());
        verify(notificationSettingRepository, times(1)).save(any(NotificationSetting.class));
    }

    @Test
    void updateNotificationSetting_whenExists() {
        UUID userId = UUID.randomUUID();
        NotificationSettingDto dto = new NotificationSettingDto();
        dto.setUserId(userId);
        NotificationSetting setting = new NotificationSetting();
        setting.setUserId(userId);

        when(notificationSettingRepository.existsById(userId)).thenReturn(true);
        when(notificationSettingRepository.save(any(NotificationSetting.class))).thenReturn(setting);

        NotificationSettingDto result = notificationSettingService.updateNotificationSetting(userId, dto);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(notificationSettingRepository, times(1)).existsById(userId);
        verify(notificationSettingRepository, times(1)).save(any(NotificationSetting.class));
    }

    @Test
    void updateNotificationSetting_whenNotExists() {
        UUID userId = UUID.randomUUID();
        NotificationSettingDto dto = new NotificationSettingDto();

        when(notificationSettingRepository.existsById(userId)).thenReturn(false);

        NotificationSettingDto result = notificationSettingService.updateNotificationSetting(userId, dto);

        assertNull(result);
        verify(notificationSettingRepository, times(1)).existsById(userId);
        verify(notificationSettingRepository, never()).save(any(NotificationSetting.class));
    }

    @Test
    void deleteNotificationSetting() {
        UUID userId = UUID.randomUUID();

        doNothing().when(notificationSettingRepository).deleteById(userId);

        notificationSettingService.deleteNotificationSetting(userId);

        verify(notificationSettingRepository, times(1)).deleteById(userId);
    }
}