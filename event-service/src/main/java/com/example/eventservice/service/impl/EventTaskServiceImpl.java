package com.example.eventservice.service.impl;

import com.example.eventservice.dto.CreateEventTaskDto;
import com.example.eventservice.dto.EventTaskDto;
import com.example.eventservice.dto.UpdateEventTaskDto;
import com.example.eventservice.exception.ResourceNotFoundException;
import com.example.eventservice.mapper.EventTaskMapper;
import com.example.eventservice.model.Event;
import com.example.eventservice.model.EventTask;
import com.example.eventservice.repository.EventRepository;
import com.example.eventservice.repository.EventTaskRepository;
import com.example.eventservice.service.EventTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventTaskServiceImpl implements EventTaskService {

    private final EventTaskRepository eventTaskRepository;
    private final EventRepository eventRepository;
    private final EventTaskMapper eventTaskMapper;

    @Override
    public List<EventTaskDto> getAllEventTasks() {
        return eventTaskRepository.findAll().stream()
                .map(eventTaskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventTaskDto getEventTaskById(UUID id) {
        return eventTaskRepository.findById(id)
                .map(eventTaskMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("EventTask not found with id: " + id));
    }

    @Override
    public EventTaskDto createEventTask(CreateEventTaskDto createEventTaskDto) {
        Event event = eventRepository.findById(createEventTaskDto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + createEventTaskDto.getEventId()));
        EventTask eventTask = eventTaskMapper.toEntity(createEventTaskDto);
        eventTask.setEvent(event);
        return eventTaskMapper.toDto(eventTaskRepository.save(eventTask));
    }

    @Override
    public EventTaskDto updateEventTask(UUID id, UpdateEventTaskDto updateEventTaskDto) {
        EventTask eventTask = eventTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EventTask not found with id: " + id));
        eventTaskMapper.updateEntity(updateEventTaskDto, eventTask);
        return eventTaskMapper.toDto(eventTaskRepository.save(eventTask));
    }

    @Override
    public void deleteEventTask(UUID id) {
        if (!eventTaskRepository.existsById(id)) {
            throw new ResourceNotFoundException("EventTask not found with id: " + id);
        }
        eventTaskRepository.deleteById(id);
    }
}