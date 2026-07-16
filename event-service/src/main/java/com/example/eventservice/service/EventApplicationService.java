package com.example.eventservice.service;

import com.example.eventservice.dto.CreateEventApplicationDto;
import com.example.eventservice.dto.EventApplicationDto;
import com.example.eventservice.dto.UpdateEventApplicationDto;

import java.util.List;
import java.util.UUID;

public interface EventApplicationService {
    List<EventApplicationDto> getAllApplications();

    EventApplicationDto getApplicationById(UUID id);

    EventApplicationDto createApplication(CreateEventApplicationDto createEventApplicationDto);

    EventApplicationDto updateApplication(UUID id, UpdateEventApplicationDto updateEventApplicationDto);

    void deleteApplication(UUID id);

    List<EventApplicationDto> getMyApplications();

    List<EventApplicationDto> getApplicationsByEventId(UUID eventId);

    EventApplicationDto teamDecision(UUID id, UpdateEventApplicationDto updateDto);

    List<EventApplicationDto> getApplicationsByTeam(UUID eventId, UUID teamId);
}