package com.example.coreservice.controller;

import com.example.coreservice.dto.ChatDto;
import com.example.coreservice.service.ChatService;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Chat", description = "Chat management APIs")
public class ChatController {
    private final ChatService chatService;

    @GetMapping
    @Operation(summary = "Get all chats")
    public ResponseEntity<List<ChatDto>> getAll() {
        return ResponseEntity.ok(chatService.getAllChats());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get chat by id")
    public ResponseEntity<ChatDto> getById(@PathVariable UUID id) {
        ChatDto chatDto = chatService.getChatById(id);
        if (chatDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(chatDto);
    }

    @PostMapping
    @Operation(summary = "Create a new chat")
    public ResponseEntity<ChatDto> create(@RequestBody ChatDto chatDto) {
        return ResponseEntity.ok(chatService.createChat(chatDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a chat")
    public ResponseEntity<ChatDto> update(@PathVariable UUID id, @RequestBody ChatDto chatDto) {
        ChatDto updatedChat = chatService.updateChat(id, chatDto);
        if (updatedChat == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedChat);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a chat")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        chatService.deleteChat(id);
        return ResponseEntity.noContent().build();
    }
}