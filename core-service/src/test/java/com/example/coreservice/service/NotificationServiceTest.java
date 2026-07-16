package com.example.coreservice.service;

import com.example.coreservice.model.entity.Notification;
import com.example.coreservice.model.entity.User;
import com.example.coreservice.repository.NotificationRepository;
import com.example.coreservice.repository.UserRepository;
import com.example.sharedlib.dto.NotificationDto;
import com.example.sharedlib.enums.NotificationType;
import com.example.sharedlib.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private UUID userId;
    private UUID notificationId;
    private User testUser;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        notificationId = UUID.randomUUID();
        testUser = User.builder().id(userId).email("test@example.com").build();
        testNotification = Notification.builder()
                .id(notificationId)
                .userId(userId)
                .type(NotificationType.SYSTEM)
                .title("Test Notification")
                .message("Test message")
                .isRead(false)
                .createdAt(OffsetDateTime.now())
                .build();
    }

    @Test
    void createNotification_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        NotificationDto result = notificationService.createNotification(
                userId, NotificationType.SYSTEM, "Test Notification", "Test message");

        assertNotNull(result);
        assertEquals("Test Notification", result.getTitle());
        assertEquals("Test message", result.getMessage());
        assertEquals(NotificationType.SYSTEM, result.getType());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void createNotification_UserNotFound_ThrowsException() {
        UUID nonExistentUserId = UUID.randomUUID();
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> notificationService.createNotification(nonExistentUserId, NotificationType.SYSTEM, "Title", "Msg"));
    }

    @Test
    void getUserNotifications_ReturnsList() {
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(List.of(testNotification));

        List<NotificationDto> result = notificationService.getUserNotifications(userId);

        assertEquals(1, result.size());
        assertEquals("Test Notification", result.get(0).getTitle());
    }

    @Test
    void getUserNotifications_ReturnsEmptyList() {
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Collections.emptyList());

        List<NotificationDto> result = notificationService.getUserNotifications(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    void getUnreadNotifications_ReturnsList() {
        when(notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId))
                .thenReturn(List.of(testNotification));

        List<NotificationDto> result = notificationService.getUnreadNotifications(userId);

        assertEquals(1, result.size());
        assertFalse(result.get(0).getIsRead());
    }

    @Test
    void markAsRead_WhenExists_MarksAsRead() {
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NotificationDto result = notificationService.markAsRead(notificationId);

        assertNotNull(result);
        assertTrue(result.getIsRead());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void markAsRead_WhenNotExists_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(notificationRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> notificationService.markAsRead(id));
    }

    @Test
    void getUnreadCount_ReturnsCount() {
        when(notificationRepository.countByUserIdAndIsReadFalse(userId)).thenReturn(5L);

        long count = notificationService.getUnreadCount(userId);

        assertEquals(5L, count);
        verify(notificationRepository, times(1)).countByUserIdAndIsReadFalse(userId);
    }
}
