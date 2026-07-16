package com.example.coreservice.service;

import com.example.coreservice.dto.MessageDto;
import com.example.coreservice.model.entity.Chat;
import com.example.coreservice.model.entity.Message;
import com.example.coreservice.repository.ChatRepository;
import com.example.coreservice.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private MessageService messageService;

    private UUID messageId;
    private UUID chatId;
    private UUID userId;
    private Chat testChat;
    private Message testMessage;
    private MessageDto testMessageDto;

    @BeforeEach
    void setUp() {
        messageId = UUID.randomUUID();
        chatId = UUID.randomUUID();
        userId = UUID.randomUUID();

        testChat = Chat.builder()
                .id(chatId)
                .name("Test Chat")
                .build();

        testMessage = Message.builder()
                .id(messageId)
                .chat(testChat)
                .userId(userId)
                .content("Hello World")
                .createdAt(OffsetDateTime.now())
                .build();

        testMessageDto = MessageDto.builder()
                .id(messageId)
                .chatId(chatId)
                .userId(userId)
                .content("Hello World")
                .createdAt(OffsetDateTime.now())
                .build();
    }

    @Test
    void getAllMessages_ReturnsList() {
        when(messageRepository.findAll()).thenReturn(List.of(testMessage));

        List<MessageDto> result = messageService.getAllMessages();

        assertEquals(1, result.size());
        assertEquals("Hello World", result.get(0).getContent());
        assertEquals(chatId, result.get(0).getChatId());
        verify(messageRepository, times(1)).findAll();
    }

    @Test
    void getAllMessages_ReturnsEmptyList() {
        when(messageRepository.findAll()).thenReturn(Collections.emptyList());

        List<MessageDto> result = messageService.getAllMessages();

        assertTrue(result.isEmpty());
    }

    @Test
    void getMessageById_WhenExists_ReturnsMessage() {
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(testMessage));

        MessageDto result = messageService.getMessageById(messageId);

        assertNotNull(result);
        assertEquals(messageId, result.getId());
        assertEquals("Hello World", result.getContent());
    }

    @Test
    void getMessageById_WhenNotExists_ReturnsNull() {
        UUID id = UUID.randomUUID();
        when(messageRepository.findById(id)).thenReturn(Optional.empty());

        MessageDto result = messageService.getMessageById(id);

        assertNull(result);
    }

    @Test
    void createMessage_Success() {
        when(chatRepository.findById(chatId)).thenReturn(Optional.of(testChat));
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage);

        MessageDto result = messageService.createMessage(testMessageDto);

        assertNotNull(result);
        assertEquals("Hello World", result.getContent());
        assertEquals(chatId, result.getChatId());
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void createMessage_ChatNotFound_ThrowsException() {
        UUID nonExistentChatId = UUID.randomUUID();
        MessageDto dto = MessageDto.builder()
                .chatId(nonExistentChatId)
                .userId(userId)
                .content("Test")
                .build();

        when(chatRepository.findById(nonExistentChatId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> messageService.createMessage(dto));
    }

    @Test
    void updateMessage_WhenExists_ReturnsUpdated() {
        when(messageRepository.existsById(messageId)).thenReturn(true);
        when(chatRepository.findById(chatId)).thenReturn(Optional.of(testChat));
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage);

        MessageDto result = messageService.updateMessage(messageId, testMessageDto);

        assertNotNull(result);
        assertEquals(messageId, result.getId());
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void updateMessage_WhenNotExists_ReturnsNull() {
        UUID id = UUID.randomUUID();
        when(messageRepository.existsById(id)).thenReturn(false);

        MessageDto result = messageService.updateMessage(id, testMessageDto);

        assertNull(result);
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void deleteMessage_CallsRepository() {
        doNothing().when(messageRepository).deleteById(messageId);

        messageService.deleteMessage(messageId);

        verify(messageRepository, times(1)).deleteById(messageId);
    }
}
