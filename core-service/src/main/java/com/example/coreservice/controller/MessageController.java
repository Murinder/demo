package com.example.coreservice.controller;

import com.example.coreservice.dto.MessageDto;
import com.example.coreservice.service.MessageService;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Messages", description = "Message management APIs")
public class MessageController {
    private final MessageService messageService;

    @GetMapping
    @Operation(summary = "Get all messages")
    public ResponseEntity<List<MessageDto>> getAll() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get message by id")
    public ResponseEntity<MessageDto> getById(@PathVariable UUID id) {
        MessageDto messageDto = messageService.getMessageById(id);
        if (messageDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(messageDto);
    }

    @PostMapping
    @Operation(summary = "Create a new message")
    public ResponseEntity<MessageDto> create(@RequestBody MessageDto messageDto) {
        return ResponseEntity.ok(messageService.createMessage(messageDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a message")
    public ResponseEntity<MessageDto> update(@PathVariable UUID id, @RequestBody MessageDto messageDto) {
        MessageDto updatedMessage = messageService.updateMessage(id, messageDto);
        if (updatedMessage == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedMessage);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a message")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}