package com.example.coreservice.service;

import com.example.coreservice.dto.ChatDto;
import com.example.coreservice.model.entity.Chat;
import com.example.coreservice.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatRepository chatRepository;

    @Transactional(readOnly = true)
    public List<ChatDto> getAllChats() {
        return chatRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChatDto getChatById(UUID id) {
        return chatRepository.findById(id)
                .map(this::mapToDto)
                .orElse(null); // Or throw an exception
    }

    public ChatDto createChat(ChatDto chatDto) {
        Chat chat = mapToEntity(chatDto);
        return mapToDto(chatRepository.save(chat));
    }

    public ChatDto updateChat(UUID id, ChatDto chatDto) {
        if (!chatRepository.existsById(id)) {
            return null; // Or throw an exception
        }
        Chat chat = mapToEntity(chatDto);
        chat.setId(id);
        return mapToDto(chatRepository.save(chat));
    }

    public void deleteChat(UUID id) {
        chatRepository.deleteById(id);
    }

    private ChatDto mapToDto(Chat chat) {
        return ChatDto.builder()
                .id(chat.getId())
                .name(chat.getName())
                .build();
    }

    private Chat mapToEntity(ChatDto chatDto) {
        Chat chat = new Chat();
        chat.setName(chatDto.getName());
        return chat;
    }
}