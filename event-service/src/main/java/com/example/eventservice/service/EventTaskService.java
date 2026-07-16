package com.example.eventservice.service;

import com.example.eventservice.dto.CreateEventTaskDto;
import com.example.eventservice.dto.EventTaskDto;
import com.example.eventservice.dto.UpdateEventTaskDto;

import java.util.List;
import java.util.UUID;

public interface EventTaskService {
    List<EventTaskDto> getAllEventTasks();

    EventTaskDto getEventTaskById(UUID id);

    EventTaskDto createEventTask(CreateEventTaskDto createEventTaskDto);

    EventTaskDto updateEventTask(UUID id, UpdateEventTaskDto updateEventTaskDto);

    void deleteEventTask(UUID id);
}