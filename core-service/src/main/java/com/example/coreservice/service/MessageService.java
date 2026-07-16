package com.example.coreservice.service;

import com.example.coreservice.dto.MessageDto;
import com.example.coreservice.model.entity.Message;
import com.example.coreservice.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.coreservice.repository.ChatRepository;
import com.example.coreservice.model.entity.Chat;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;

    @Transactional(readOnly = true)
    public List<MessageDto> getAllMessages() {
        return messageRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MessageDto getMessageById(UUID id) {
        return messageRepository.findById(id)
                .map(this::mapToDto)
                .orElse(null);
    }

    public MessageDto createMessage(MessageDto messageDto) {
        Message message = mapToEntity(messageDto);
        return mapToDto(messageRepository.save(message));
    }

    public MessageDto updateMessage(UUID id, MessageDto messageDto) {
        if (!messageRepository.existsById(id)) {
            return null;
        }
        Message message = mapToEntity(messageDto);
        message.setId(id);
        return mapToDto(messageRepository.save(message));
    }

    public void deleteMessage(UUID id) {
        messageRepository.deleteById(id);
    }

    private MessageDto mapToDto(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .chatId(message.getChat().getId())
                .userId(message.getUserId())
                .content(message.getContent())
                .filePath(message.getFilePath())
                .createdAt(message.getCreatedAt())
                .build();
    }

    private Message mapToEntity(MessageDto messageDto) {
        Chat chat = chatRepository.findById(messageDto.getChatId())
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        return Message.builder()
                .id(messageDto.getId())
                .chat(chat)
                .userId(messageDto.getUserId())
                .content(messageDto.getContent())
                .filePath(messageDto.getFilePath())
                .createdAt(messageDto.getCreatedAt())
                .build();
    }
}