package com.example.coreservice.service;

import com.example.sharedlib.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotificationToUser(UUID userId, NotificationDto notification) {
        log.debug("Sending WebSocket notification to user {}: {}", userId, notification.getTitle());
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, notification);
    }

    public void sendChatMessage(UUID chatId, Object message) {
        log.debug("Broadcasting message to chat {}", chatId);
        messagingTemplate.convertAndSend("/topic/chat/" + chatId, message);
    }
}
