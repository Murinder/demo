package com.example.eventservice.service;

import com.example.eventservice.dto.CreateEventMentorDto;
import com.example.eventservice.dto.EventMentorDto;
import com.example.eventservice.exception.ResourceNotFoundException;
import com.example.eventservice.mapper.EventMentorMapper;
import com.example.eventservice.model.Event;
import com.example.eventservice.model.EventMentor;
import com.example.eventservice.repository.EventMentorRepository;
import com.example.eventservice.repository.EventRepository;
import com.example.eventservice.service.impl.EventMentorServiceImpl;
import com.example.sharedlib.enums.EventFormat;
import com.example.sharedlib.enums.EventStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventMentorServiceImplTest {

    @Mock
    private EventMentorRepository eventMentorRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMentorMapper eventMentorMapper;

    @InjectMocks
    private EventMentorServiceImpl eventMentorService;

    private Event buildEvent() {
        return Event.builder()
                .id(UUID.randomUUID())
                .title("Hackathon")
                .startDate(OffsetDateTime.now())
                .endDate(OffsetDateTime.now().plusDays(2))
                .format(EventFormat.ONLINE)
                .status(EventStatus.DRAFT)
                .createdBy(UUID.randomUUID())
                .build();
    }

    @Test
    void createEventMentor_ReturnsDtoAndSaves() {
        Event event = buildEvent();
        UUID userId = UUID.randomUUID();
        CreateEventMentorDto createDto = CreateEventMentorDto.builder()
                .eventId(event.getId())
                .userId(userId)
                .build();
        EventMentor.EventMentorId compositeId = new EventMentor.EventMentorId(event.getId(), userId);
        EventMentor mentor = EventMentor.builder()
                .id(compositeId)
                .event(event)
                .build();
        EventMentorDto dto = EventMentorDto.builder()
                .eventId(event.getId())
                .userId(userId)
                .build();

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(eventMentorMapper.toEntity(createDto)).thenReturn(mentor);
        when(eventMentorRepository.save(mentor)).thenReturn(mentor);
        when(eventMentorMapper.toDto(mentor)).thenReturn(dto);

        EventMentorDto result = eventMentorService.createEventMentor(createDto);

        assertNotNull(result);
        assertEquals(event.getId(), result.getEventId());
        assertEquals(userId, result.getUserId());
        verify(eventMentorRepository).save(mentor);
    }

    @Test
    void createEventMentor_ThrowsResourceNotFoundException_WhenEventNotFound() {
        UUID eventId = UUID.randomUUID();
        CreateEventMentorDto createDto = CreateEventMentorDto.builder()
                .eventId(eventId)
                .userId(UUID.randomUUID())
                .build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventMentorService.createEventMentor(createDto));
    }

    @Test
    void getEventMentorById_ReturnsDto_WhenFound() {
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        EventMentor.EventMentorId compositeId = new EventMentor.EventMentorId(eventId, userId);
        EventMentor mentor = EventMentor.builder()
                .id(compositeId)
                .expertise("Java")
                .build();
        EventMentorDto dto = EventMentorDto.builder()
                .eventId(eventId)
                .userId(userId)
                .build();

        when(eventMentorRepository.findById(compositeId)).thenReturn(Optional.of(mentor));
        when(eventMentorMapper.toDto(mentor)).thenReturn(dto);

        EventMentorDto result = eventMentorService.getEventMentorById(compositeId);

        assertNotNull(result);
        assertEquals(eventId, result.getEventId());
    }

    @Test
    void getEventMentorById_ThrowsResourceNotFoundException_WhenNotFound() {
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        EventMentor.EventMentorId compositeId = new EventMentor.EventMentorId(eventId, userId);

        when(eventMentorRepository.findById(compositeId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventMentorService.getEventMentorById(compositeId));
    }

    @Test
    void deleteEventMentor_CallsRepositoryDeleteById() {
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        EventMentor.EventMentorId compositeId = new EventMentor.EventMentorId(eventId, userId);

        when(eventMentorRepository.existsById(compositeId)).thenReturn(true);

        eventMentorService.deleteEventMentor(compositeId);

        verify(eventMentorRepository).deleteById(compositeId);
    }

    @Test
    void deleteEventMentor_ThrowsResourceNotFoundException_WhenNotFound() {
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        EventMentor.EventMentorId compositeId = new EventMentor.EventMentorId(eventId, userId);

        when(eventMentorRepository.existsById(compositeId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> eventMentorService.deleteEventMentor(compositeId));
    }

    @Test
    void getAllEventMentors_ReturnsEmptyList() {
        when(eventMentorRepository.findAll()).thenReturn(Collections.emptyList());

        List<EventMentorDto> result = eventMentorService.getAllEventMentors();

        assertTrue(result.isEmpty());
    }
}
