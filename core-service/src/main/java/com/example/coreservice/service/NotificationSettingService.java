package com.example.coreservice.service;

import com.example.coreservice.dto.NotificationSettingDto;
import com.example.coreservice.model.entity.NotificationSetting;
import com.example.coreservice.repository.NotificationSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationSettingService {

    private final NotificationSettingRepository notificationSettingRepository;

    public List<NotificationSettingDto> getAllNotificationSettings() {
        return notificationSettingRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public NotificationSettingDto getNotificationSettingByUserId(UUID userId) {
        return notificationSettingRepository.findById(userId)
                .map(this::mapToDto)
                .orElse(null);
    }

    public NotificationSettingDto createNotificationSetting(NotificationSettingDto notificationSettingDto) {
        NotificationSetting notificationSetting = mapToEntity(notificationSettingDto);
        return mapToDto(notificationSettingRepository.save(notificationSetting));
    }

    public NotificationSettingDto updateNotificationSetting(UUID userId, NotificationSettingDto notificationSettingDto) {
        if (!notificationSettingRepository.existsById(userId)) {
            return null;
        }
        NotificationSetting notificationSetting = mapToEntity(notificationSettingDto);
        notificationSetting.setUserId(userId);
        return mapToDto(notificationSettingRepository.save(notificationSetting));
    }

    public void deleteNotificationSetting(UUID userId) {
        notificationSettingRepository.deleteById(userId);
    }

    private NotificationSettingDto mapToDto(NotificationSetting notificationSetting) {
        return NotificationSettingDto.builder()
                .userId(notificationSetting.getUserId())
                .emailEnabled(notificationSetting.isEmailEnabled())
                .inAppEnabled(notificationSetting.isInAppEnabled())
                .pushEnabled(notificationSetting.isPushEnabled())
                .updatedAt(notificationSetting.getUpdatedAt())
                .build();
    }

    private NotificationSetting mapToEntity(NotificationSettingDto notificationSettingDto) {
        return NotificationSetting.builder()
                .userId(notificationSettingDto.getUserId())
                .emailEnabled(notificationSettingDto.isEmailEnabled())
                .inAppEnabled(notificationSettingDto.isInAppEnabled())
                .pushEnabled(notificationSettingDto.isPushEnabled())
                .updatedAt(notificationSettingDto.getUpdatedAt())
                .build();
    }
}