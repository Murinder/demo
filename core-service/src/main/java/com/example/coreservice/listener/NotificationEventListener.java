package com.example.coreservice.listener;

import com.example.coreservice.service.NotificationService;
import com.example.coreservice.service.NotificationWebSocketService;
import com.example.sharedlib.config.RabbitMqAutoConfiguration;
import com.example.sharedlib.dto.NotificationDto;
import com.example.sharedlib.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final NotificationWebSocketService webSocketService;

    @RabbitListener(queues = RabbitMqAutoConfiguration.QUEUE_NOTIFICATION_SEND)
    public void onNotificationSend(Map<String, Object> event) {
        log.info("Received notification command: {}", event);

        String userIdStr = String.valueOf(event.get("userId"));
        String title = (String) event.getOrDefault("title", "Notification");
        String message = (String) event.getOrDefault("message", "");
        String typeStr = (String) event.getOrDefault("type", "SYSTEM");

        UUID userId = UUID.fromString(userIdStr);
        NotificationType type = NotificationType.valueOf(typeStr);

        NotificationDto notification = notificationService.createNotification(userId, type, title, message);
        webSocketService.sendNotificationToUser(userId, notification);
    }
}
