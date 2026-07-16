package com.example.eventservice.controller;

import com.example.eventservice.dto.CreateEventMentorDto;
import com.example.eventservice.dto.EventMentorDto;
import com.example.eventservice.model.EventMentor;
import com.example.eventservice.service.EventMentorService;
import com.example.sharedlib.security.AuthenticatedOnly;
import com.example.sharedlib.security.LecturerOrAbove;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/v1/event-mentors")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Event Mentors", description = "Управление менторами мероприятий")
public class EventMentorController {

    private final EventMentorService eventMentorService;

    @Operation(summary = "Get all event mentors")
    @GetMapping
    public ResponseEntity<List<EventMentorDto>> getAllEventMentors() {
        return ResponseEntity.ok(eventMentorService.getAllEventMentors());
    }

    @Operation(summary = "Get event mentor by event and user ID")
    @GetMapping("/{eventId}/{userId}")
    public ResponseEntity<EventMentorDto> getEventMentorById(@PathVariable UUID eventId, @PathVariable UUID userId) {
        EventMentor.EventMentorId id = new EventMentor.EventMentorId(eventId, userId);
        return ResponseEntity.ok(eventMentorService.getEventMentorById(id));
    }

    @Operation(summary = "Assign a mentor to an event")
    @LecturerOrAbove
    @PostMapping
    public ResponseEntity<EventMentorDto> createEventMentor(@RequestBody CreateEventMentorDto createEventMentorDto) {
        return new ResponseEntity<>(eventMentorService.createEventMentor(createEventMentorDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Remove a mentor from an event")
    @LecturerOrAbove
    @DeleteMapping("/{eventId}/{userId}")
    public ResponseEntity<Void> deleteEventMentor(@PathVariable UUID eventId, @PathVariable UUID userId) {
        EventMentor.EventMentorId id = new EventMentor.EventMentorId(eventId, userId);
        eventMentorService.deleteEventMentor(id);
        return ResponseEntity.noContent().build();
    }
}