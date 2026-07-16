package com.example.eventservice.service;

import com.example.eventservice.dto.CreateEventMentorDto;
import com.example.eventservice.dto.EventMentorDto;
import com.example.eventservice.model.EventMentor;

import java.util.List;

public interface EventMentorService {
    List<EventMentorDto> getAllEventMentors();

    EventMentorDto getEventMentorById(EventMentor.EventMentorId id);

    EventMentorDto createEventMentor(CreateEventMentorDto createEventMentorDto);

    void deleteEventMentor(EventMentor.EventMentorId id);
}