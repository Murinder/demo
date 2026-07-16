package com.example.coreservice.service;

import com.example.coreservice.dto.ChatDto;
import com.example.coreservice.model.entity.Chat;
import com.example.coreservice.repository.ChatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private ChatService chatService;

    private UUID chatId;
    private Chat testChat;
    private ChatDto testChatDto;

    @BeforeEach
    void setUp() {
        chatId = UUID.randomUUID();
        testChat = Chat.builder()
                .id(chatId)
                .name("Test Chat")
                .build();
        testChatDto = ChatDto.builder()
                .id(chatId)
                .name("Test Chat")
                .build();
    }

    @Test
    void getAllChats_ReturnsList() {
        when(chatRepository.findAll()).thenReturn(List.of(testChat));

        List<ChatDto> result = chatService.getAllChats();

        assertEquals(1, result.size());
        assertEquals("Test Chat", result.get(0).getName());
        verify(chatRepository, times(1)).findAll();
    }

    @Test
    void getAllChats_ReturnsEmptyList() {
        when(chatRepository.findAll()).thenReturn(Collections.emptyList());

        List<ChatDto> result = chatService.getAllChats();

        assertTrue(result.isEmpty());
        verify(chatRepository, times(1)).findAll();
    }

    @Test
    void getChatById_WhenExists_ReturnsChat() {
        when(chatRepository.findById(chatId)).thenReturn(Optional.of(testChat));

        ChatDto result = chatService.getChatById(chatId);

        assertNotNull(result);
        assertEquals(chatId, result.getId());
        assertEquals("Test Chat", result.getName());
    }

    @Test
    void getChatById_WhenNotExists_ReturnsNull() {
        UUID id = UUID.randomUUID();
        when(chatRepository.findById(id)).thenReturn(Optional.empty());

        ChatDto result = chatService.getChatById(id);

        assertNull(result);
    }

    @Test
    void createChat_Success() {
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> {
            Chat chat = invocation.getArgument(0);
            chat.setId(chatId);
            return chat;
        });

        ChatDto result = chatService.createChat(testChatDto);

        assertNotNull(result);
        assertEquals("Test Chat", result.getName());
        verify(chatRepository, times(1)).save(any(Chat.class));
    }

    @Test
    void updateChat_WhenExists_ReturnsUpdatedChat() {
        when(chatRepository.existsById(chatId)).thenReturn(true);
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChatDto updateDto = ChatDto.builder().name("Updated Chat").build();
        ChatDto result = chatService.updateChat(chatId, updateDto);

        assertNotNull(result);
        assertEquals(chatId, result.getId());
        verify(chatRepository, times(1)).save(any(Chat.class));
    }

    @Test
    void updateChat_WhenNotExists_ReturnsNull() {
        UUID id = UUID.randomUUID();
        when(chatRepository.existsById(id)).thenReturn(false);

        ChatDto result = chatService.updateChat(id, testChatDto);

        assertNull(result);
        verify(chatRepository, never()).save(any(Chat.class));
    }

    @Test
    void deleteChat_CallsRepository() {
        doNothing().when(chatRepository).deleteById(chatId);

        chatService.deleteChat(chatId);

        verify(chatRepository, times(1)).deleteById(chatId);
    }
}
