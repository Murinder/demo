package com.example.eventservice.service.impl;

import com.example.eventservice.dto.CreateEventMentorDto;
import com.example.eventservice.dto.EventMentorDto;
import com.example.eventservice.exception.ResourceNotFoundException;
import com.example.eventservice.mapper.EventMentorMapper;
import com.example.eventservice.model.Event;
import com.example.eventservice.model.EventMentor;
import com.example.eventservice.repository.EventMentorRepository;
import com.example.eventservice.repository.EventRepository;
import com.example.eventservice.service.EventMentorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventMentorServiceImpl implements EventMentorService {

    private final EventMentorRepository eventMentorRepository;
    private final EventRepository eventRepository;
    private final EventMentorMapper eventMentorMapper;

    @Override
    public List<EventMentorDto> getAllEventMentors() {
        return eventMentorRepository.findAll().stream()
                .map(eventMentorMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventMentorDto getEventMentorById(EventMentor.EventMentorId id) {
        return eventMentorRepository.findById(id)
                .map(eventMentorMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("EventMentor not found with id: " + id));
    }

    @Override
    public EventMentorDto createEventMentor(CreateEventMentorDto createEventMentorDto) {
        Event event = eventRepository.findById(createEventMentorDto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + createEventMentorDto.getEventId()));
        EventMentor eventMentor = eventMentorMapper.toEntity(createEventMentorDto);
        eventMentor.setEvent(event);
        return eventMentorMapper.toDto(eventMentorRepository.save(eventMentor));
    }

    @Override
    public void deleteEventMentor(EventMentor.EventMentorId id) {
        if (!eventMentorRepository.existsById(id)) {
            throw new ResourceNotFoundException("EventMentor not found with id: " + id);
        }
        eventMentorRepository.deleteById(id);
    }
}