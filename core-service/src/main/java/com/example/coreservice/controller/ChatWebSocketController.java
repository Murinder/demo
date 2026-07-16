package com.example.coreservice.controller;

import com.example.coreservice.dto.MessageDto;
import com.example.coreservice.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;

    @MessageMapping("/chat/{chatId}")
    @SendTo("/topic/chat/{chatId}")
    public MessageDto sendMessage(@DestinationVariable UUID chatId, MessageDto messageDto) {
        log.info("WebSocket message in chat {}: {}", chatId, messageDto.getContent());
        messageDto.setChatId(chatId);
        return messageService.createMessage(messageDto);
    }
}
