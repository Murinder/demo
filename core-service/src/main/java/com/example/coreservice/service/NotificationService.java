package com.example.coreservice.service;

import com.example.sharedlib.enums.NotificationType;
import com.example.sharedlib.exception.ResourceNotFoundException;
import com.example.sharedlib.dto.NotificationDto;
import com.example.coreservice.model.entity.Notification;
import com.example.coreservice.model.entity.User;
import com.example.coreservice.repository.NotificationRepository;
import com.example.coreservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service для управления уведомлениями
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * Создать уведомление для пользователя
     */
    public NotificationDto createNotification(UUID userId, NotificationType type, String title, String message) {
        log.info("Creating notification for user: {}, type: {}", userId, type);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Notification notification = Notification.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .type(type)
                .title(title)
                .message(message)
                .isRead(false)
                .createdAt(OffsetDateTime.now())
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        return mapToDto(savedNotification);
    }

    /**
     * Получить все уведомления пользователя
     */
    @Transactional(readOnly = true)
    public List<NotificationDto> getUserNotifications(UUID userId) {
        log.info("Fetching notifications for user: {}", userId);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Получить непрочитанные уведомления
     */
    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadNotifications(UUID userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Отметить уведомление как прочитанное
     */
    public NotificationDto markAsRead(UUID notificationId) {
        log.info("Marking notification as read: {}", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        notification.setRead(true);

        Notification updatedNotification = notificationRepository.save(notification);
        return mapToDto(updatedNotification);
    }

    /**
     * Получить количество непрочитанных уведомлений
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    private NotificationDto mapToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}