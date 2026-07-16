package com.example.coreservice.controller;

import com.example.coreservice.model.dto.SessionDto;
import com.example.coreservice.service.SessionService;
import com.example.sharedlib.security.AdminOnly;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Sessions", description = "API for managing sessions")
public class SessionController {

    private final SessionService sessionService;

    @AdminOnly
    @GetMapping
    @Operation(summary = "Get all sessions")
    public ResponseEntity<List<SessionDto>> getAll() {
        return ResponseEntity.ok(sessionService.getAllSessions());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get session by ID")
    public ResponseEntity<SessionDto> getById(@PathVariable UUID id) {
        SessionDto sessionDto = sessionService.getSessionById(id);
        return sessionDto != null ? ResponseEntity.ok(sessionDto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Create a new session")
    public ResponseEntity<SessionDto> create(@RequestBody SessionDto sessionDto) {
        return ResponseEntity.ok(sessionService.createSession(sessionDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing session")
    public ResponseEntity<SessionDto> update(@PathVariable UUID id, @RequestBody SessionDto sessionDto) {
        SessionDto updatedSession = sessionService.updateSession(id, sessionDto);
        return updatedSession != null ? ResponseEntity.ok(updatedSession) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a session")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        sessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }
}