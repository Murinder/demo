package com.example.eventservice.controller;

import com.example.eventservice.dto.CreateEventDto;
import com.example.eventservice.dto.EventApplicationDto;
import com.example.eventservice.dto.TeamDto;
import com.example.sharedlib.dto.EventDto;
import com.example.eventservice.dto.UpdateEventDto;
import com.example.eventservice.service.EventApplicationService;
import com.example.eventservice.service.EventService;
import com.example.eventservice.service.TeamService;
import com.example.sharedlib.security.AdminOnly;
import com.example.sharedlib.security.AuthenticatedOnly;
import com.example.sharedlib.security.LecturerOrAbove;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Events", description = "Управление мероприятиями платформы")
public class EventController {

    private final EventService eventService;
    private final EventApplicationService applicationService;
    private final TeamService teamService;

    @Operation(summary = "Get all events")
    @GetMapping
    public ResponseEntity<List<EventDto>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @Operation(summary = "Get event by ID")
    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEventById(@PathVariable UUID id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @Operation(summary = "Create a new event")
    @LecturerOrAbove
    @PostMapping
    public ResponseEntity<EventDto> createEvent(@RequestBody CreateEventDto createEventDto) {
        return new ResponseEntity<>(eventService.createEvent(createEventDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Update an event")
    @LecturerOrAbove
    @PutMapping("/{id}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable UUID id, @RequestBody UpdateEventDto updateEventDto) {
        return ResponseEntity.ok(eventService.updateEvent(id, updateEventDto));
    }

    @Operation(summary = "Delete an event")
    @LecturerOrAbove
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get applications for an event")
    @LecturerOrAbove
    @GetMapping("/{id}/applications")
    public ResponseEntity<List<EventApplicationDto>> getApplicationsByEvent(@PathVariable UUID id) {
        return ResponseEntity.ok(applicationService.getApplicationsByEventId(id));
    }

    @Operation(summary = "Get teams for an event")
    @GetMapping("/{id}/teams")
    public ResponseEntity<List<TeamDto>> getTeamsByEvent(@PathVariable UUID id) {
        return ResponseEntity.ok(teamService.getTeamsByEventId(id));
    }
}