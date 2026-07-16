package com.example.coreservice.integration;

import java.util.UUID;

/**
 * Interface for Telegram Bot integration.
 */
public interface TelegramBotService {
    void sendNotification(UUID userId, String message);
    void linkAccount(UUID userId, String telegramChatId);
}
