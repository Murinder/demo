package com.example.coreservice.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Stub implementation of Telegram Bot service.
 * Logs calls instead of actually sending messages.
 */
@Slf4j
@Service
public class StubTelegramBotService implements TelegramBotService {

    @Override
    public void sendNotification(UUID userId, String message) {
        log.info("[STUB TELEGRAM] Sending notification to user {}: {}", userId, message);
    }

    @Override
    public void linkAccount(UUID userId, String telegramChatId) {
        log.info("[STUB TELEGRAM] Linking user {} to Telegram chat {}", userId, telegramChatId);
    }
}
