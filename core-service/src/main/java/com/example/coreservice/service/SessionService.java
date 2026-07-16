package com.example.coreservice.service;

import com.example.coreservice.model.dto.SessionDto;
import com.example.coreservice.model.entity.Session;
import com.example.coreservice.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    public List<SessionDto> getAllSessions() {
        return sessionRepository.findAll().stream().map(this::toDto).toList();
    }

    public SessionDto getSessionById(UUID id) {
        return sessionRepository.findById(id).map(this::toDto).orElse(null);
    }

    public SessionDto createSession(SessionDto sessionDto) {
        Session session = toEntity(sessionDto);
        return toDto(sessionRepository.save(session));
    }

    public SessionDto updateSession(UUID id, SessionDto sessionDto) {
        if (!sessionRepository.existsById(id)) {
            return null;
        }
        Session session = toEntity(sessionDto);
        session.setId(id);
        return toDto(sessionRepository.save(session));
    }

    public void deleteSession(UUID id) {
        sessionRepository.deleteById(id);
    }

    private SessionDto toDto(Session session) {
        return new SessionDto(
                session.getId(),
                session.getUserId(),
                session.getToken(),
                session.getExpiresAt(),
                session.getCreatedAt(),
                session.getIpAddress(),
                session.getUserAgent(),
                session.getStatus()
        );
    }

    private Session toEntity(SessionDto sessionDto) {
        return Session.builder()
                .id(sessionDto.id())
                .userId(sessionDto.userId())
                .token(sessionDto.token())
                .expiresAt(sessionDto.expiresAt())
                .createdAt(sessionDto.createdAt())
                .ipAddress(sessionDto.ipAddress())
                .userAgent(sessionDto.userAgent())
                .status(sessionDto.status())
                .build();
    }
}