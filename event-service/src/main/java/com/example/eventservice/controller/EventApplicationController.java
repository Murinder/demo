package com.example.eventservice.controller;

import com.example.eventservice.dto.CreateEventApplicationDto;
import com.example.eventservice.dto.EventApplicationDto;
import com.example.eventservice.dto.UpdateEventApplicationDto;
import com.example.eventservice.service.EventApplicationService;
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
@RequestMapping("/api/v1/event-applications")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Event Applications", description = "Управление заявками на мероприятия")
public class EventApplicationController {

    private final EventApplicationService applicationService;

    @Operation(summary = "Get all event applications")
    @LecturerOrAbove
    @GetMapping
    public ResponseEntity<List<EventApplicationDto>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    @Operation(summary = "Get event application by ID")
    @GetMapping("/{id}")
    public ResponseEntity<EventApplicationDto> getApplicationById(@PathVariable UUID id) {
        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }

    @Operation(summary = "Create a new event application")
    @PostMapping
    public ResponseEntity<EventApplicationDto> createApplication(@RequestBody CreateEventApplicationDto createDto) {
        return new ResponseEntity<>(applicationService.createApplication(createDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Update an event application")
    @LecturerOrAbove
    @PutMapping("/{id}")
    public ResponseEntity<EventApplicationDto> updateApplication(@PathVariable UUID id, @RequestBody UpdateEventApplicationDto updateDto) {
        return ResponseEntity.ok(applicationService.updateApplication(id, updateDto));
    }

    @Operation(summary = "Delete an event application (owner or lecturer)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable UUID id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get my event applications")
    @GetMapping("/my")
    public ResponseEntity<List<EventApplicationDto>> getMyApplications() {
        return ResponseEntity.ok(applicationService.getMyApplications());
    }

    @Operation(summary = "Team leader accepts or rejects a join request")
    @PutMapping("/{id}/team-decision")
    public ResponseEntity<EventApplicationDto> teamDecision(@PathVariable UUID id, @RequestBody UpdateEventApplicationDto updateDto) {
        return ResponseEntity.ok(applicationService.teamDecision(id, updateDto));
    }

    @Operation(summary = "Get applications for a specific team in an event")
    @GetMapping("/by-team")
    public ResponseEntity<List<EventApplicationDto>> getApplicationsByTeam(
            @RequestParam UUID eventId, @RequestParam UUID teamId) {
        return ResponseEntity.ok(applicationService.getApplicationsByTeam(eventId, teamId));
    }
}