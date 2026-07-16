package com.example.coreservice.service;

import com.example.coreservice.model.dto.SessionDto;
import com.example.coreservice.model.entity.Session;
import com.example.coreservice.model.enums.SessionStatus;
import com.example.coreservice.repository.SessionRepository;
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
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionService sessionService;

    private UUID sessionId;
    private UUID userId;
    private Session testSession;
    private SessionDto testSessionDto;
    private OffsetDateTime now;

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID();
        userId = UUID.randomUUID();
        now = OffsetDateTime.now();

        testSession = Session.builder()
                .id(sessionId)
                .userId(userId)
                .token("test-token-123")
                .expiresAt(now.plusHours(1))
                .createdAt(now)
                .ipAddress("127.0.0.1")
                .userAgent("TestBrowser/1.0")
                .status(SessionStatus.ACTIVE)
                .build();

        testSessionDto = new SessionDto(
                sessionId,
                userId,
                "test-token-123",
                now.plusHours(1),
                now,
                "127.0.0.1",
                "TestBrowser/1.0",
                SessionStatus.ACTIVE
        );
    }

    @Test
    void getAllSessions_ReturnsList() {
        when(sessionRepository.findAll()).thenReturn(List.of(testSession));

        List<SessionDto> result = sessionService.getAllSessions();

        assertEquals(1, result.size());
        assertEquals(sessionId, result.get(0).id());
        assertEquals("test-token-123", result.get(0).token());
        verify(sessionRepository, times(1)).findAll();
    }

    @Test
    void getAllSessions_ReturnsEmptyList() {
        when(sessionRepository.findAll()).thenReturn(Collections.emptyList());

        List<SessionDto> result = sessionService.getAllSessions();

        assertTrue(result.isEmpty());
    }

    @Test
    void getSessionById_WhenExists_ReturnsSession() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));

        SessionDto result = sessionService.getSessionById(sessionId);

        assertNotNull(result);
        assertEquals(sessionId, result.id());
        assertEquals(userId, result.userId());
        assertEquals(SessionStatus.ACTIVE, result.status());
    }

    @Test
    void getSessionById_WhenNotExists_ReturnsNull() {
        UUID id = UUID.randomUUID();
        when(sessionRepository.findById(id)).thenReturn(Optional.empty());

        SessionDto result = sessionService.getSessionById(id);

        assertNull(result);
    }

    @Test
    void createSession_Success() {
        when(sessionRepository.save(any(Session.class))).thenReturn(testSession);

        SessionDto result = sessionService.createSession(testSessionDto);

        assertNotNull(result);
        assertEquals(sessionId, result.id());
        assertEquals("test-token-123", result.token());
        assertEquals(SessionStatus.ACTIVE, result.status());
        verify(sessionRepository, times(1)).save(any(Session.class));
    }

    @Test
    void updateSession_WhenExists_ReturnsUpdated() {
        when(sessionRepository.existsById(sessionId)).thenReturn(true);
        when(sessionRepository.save(any(Session.class))).thenReturn(testSession);

        SessionDto result = sessionService.updateSession(sessionId, testSessionDto);

        assertNotNull(result);
        assertEquals(sessionId, result.id());
        verify(sessionRepository, times(1)).save(any(Session.class));
    }

    @Test
    void updateSession_WhenNotExists_ReturnsNull() {
        UUID id = UUID.randomUUID();
        when(sessionRepository.existsById(id)).thenReturn(false);

        SessionDto result = sessionService.updateSession(id, testSessionDto);

        assertNull(result);
        verify(sessionRepository, never()).save(any(Session.class));
    }

    @Test
    void deleteSession_CallsRepository() {
        doNothing().when(sessionRepository).deleteById(sessionId);

        sessionService.deleteSession(sessionId);

        verify(sessionRepository, times(1)).deleteById(sessionId);
    }
}
