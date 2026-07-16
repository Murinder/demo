package com.example.eventservice.controller;

import com.example.eventservice.dto.CreateEventTaskDto;
import com.example.eventservice.dto.EventTaskDto;
import com.example.eventservice.dto.UpdateEventTaskDto;
import com.example.eventservice.service.EventTaskService;
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
@RequestMapping("/api/v1/event-tasks")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Event Tasks", description = "Управление задачами мероприятий")
public class EventTaskController {

    private final EventTaskService eventTaskService;

    @Operation(summary = "Get all event tasks")
    @GetMapping
    public ResponseEntity<List<EventTaskDto>> getAllEventTasks() {
        return ResponseEntity.ok(eventTaskService.getAllEventTasks());
    }

    @Operation(summary = "Get event task by ID")
    @GetMapping("/{id}")
    public ResponseEntity<EventTaskDto> getEventTaskById(@PathVariable UUID id) {
        return ResponseEntity.ok(eventTaskService.getEventTaskById(id));
    }

    @Operation(summary = "Create a new event task")
    @LecturerOrAbove
    @PostMapping
    public ResponseEntity<EventTaskDto> createEventTask(@RequestBody CreateEventTaskDto createEventTaskDto) {
        return new ResponseEntity<>(eventTaskService.createEventTask(createEventTaskDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Update an event task")
    @LecturerOrAbove
    @PutMapping("/{id}")
    public ResponseEntity<EventTaskDto> updateEventTask(@PathVariable UUID id, @RequestBody UpdateEventTaskDto updateEventTaskDto) {
        return ResponseEntity.ok(eventTaskService.updateEventTask(id, updateEventTaskDto));
    }

    @Operation(summary = "Delete an event task")
    @LecturerOrAbove
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventTask(@PathVariable UUID id) {
        eventTaskService.deleteEventTask(id);
        return ResponseEntity.noContent().build();
    }
}