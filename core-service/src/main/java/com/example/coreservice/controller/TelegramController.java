package com.example.coreservice.controller;

import com.example.coreservice.integration.TelegramBotService;
import com.example.sharedlib.response.ApiResponse;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/integrations/telegram")
@RequiredArgsConstructor
@Tag(name = "Telegram Integration", description = "Telegram Bot integration endpoints")
public class TelegramController {

    private final TelegramBotService telegramBotService;

    @AuthenticatedOnly
    @PostMapping("/link")
    @Operation(summary = "Link Telegram account", description = "Links a Telegram chat ID to the user account")
    public ResponseEntity<ApiResponse<Void>> linkAccount(@RequestBody Map<String, String> payload) {
        UUID userId = UUID.fromString(payload.get("userId"));
        String chatId = payload.get("telegramChatId");
        log.info("Linking Telegram account for user: {}", userId);

        telegramBotService.linkAccount(userId, chatId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .code("TELEGRAM_LINKED")
                .message("Telegram account linked successfully")
                .build());
    }
}
