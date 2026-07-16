package com.example.eventservice.service;

import com.example.eventservice.dto.CreateEventDto;
import com.example.eventservice.dto.UpdateEventDto;
import com.example.sharedlib.dto.EventDto;

import java.util.List;
import java.util.UUID;

public interface EventService {
    List<EventDto> getAllEvents();

    EventDto getEventById(UUID id);

    EventDto createEvent(CreateEventDto createEventDto);

    EventDto updateEvent(UUID id, UpdateEventDto updateEventDto);

    void deleteEvent(UUID id);
}