package com.example.eventservice.service;

import com.example.eventservice.dto.CreateEventTaskDto;
import com.example.eventservice.dto.EventTaskDto;
import com.example.eventservice.dto.UpdateEventTaskDto;
import com.example.eventservice.exception.ResourceNotFoundException;
import com.example.eventservice.mapper.EventTaskMapper;
import com.example.eventservice.model.Event;
import com.example.eventservice.model.EventTask;
import com.example.eventservice.repository.EventRepository;
import com.example.eventservice.repository.EventTaskRepository;
import com.example.eventservice.service.impl.EventTaskServiceImpl;
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
class EventTaskServiceImplTest {

    @Mock
    private EventTaskRepository eventTaskRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventTaskMapper eventTaskMapper;

    @InjectMocks
    private EventTaskServiceImpl eventTaskService;

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
    void createEventTask_ReturnsDtoAndSaves() {
        Event event = buildEvent();
        UUID taskId = UUID.randomUUID();
        CreateEventTaskDto createDto = CreateEventTaskDto.builder()
                .eventId(event.getId())
                .title("Build API")
                .description("Build a REST API")
                .difficulty("medium")
                .points(100)
                .build();
        EventTask task = EventTask.builder()
                .id(taskId)
                .event(event)
                .title("Build API")
                .description("Build a REST API")
                .difficulty("medium")
                .points(100)
                .build();
        EventTaskDto dto = EventTaskDto.builder()
                .id(taskId)
                .eventId(event.getId())
                .title("Build API")
                .points(100)
                .build();

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(eventTaskMapper.toEntity(createDto)).thenReturn(task);
        when(eventTaskRepository.save(task)).thenReturn(task);
        when(eventTaskMapper.toDto(task)).thenReturn(dto);

        EventTaskDto result = eventTaskService.createEventTask(createDto);

        assertNotNull(result);
        assertEquals("Build API", result.getTitle());
        assertEquals(100, result.getPoints());
        verify(eventTaskRepository).save(task);
    }

    @Test
    void createEventTask_ThrowsResourceNotFoundException_WhenEventNotFound() {
        UUID eventId = UUID.randomUUID();
        CreateEventTaskDto createDto = CreateEventTaskDto.builder()
                .eventId(eventId)
                .title("Task")
                .description("Desc")
                .points(50)
                .build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventTaskService.createEventTask(createDto));
    }

    @Test
    void getEventTaskById_ReturnsDto_WhenFound() {
        UUID taskId = UUID.randomUUID();
        EventTask task = EventTask.builder()
                .id(taskId)
                .title("Task")
                .description("Desc")
                .points(50)
                .build();
        EventTaskDto dto = EventTaskDto.builder()
                .id(taskId)
                .title("Task")
                .points(50)
                .build();

        when(eventTaskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(eventTaskMapper.toDto(task)).thenReturn(dto);

        EventTaskDto result = eventTaskService.getEventTaskById(taskId);

        assertNotNull(result);
        assertEquals(taskId, result.getId());
    }

    @Test
    void getEventTaskById_ThrowsResourceNotFoundException_WhenNotFound() {
        UUID taskId = UUID.randomUUID();
        when(eventTaskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventTaskService.getEventTaskById(taskId));
    }

    @Test
    void updateEventTask_ReturnsUpdatedDto() {
        UUID taskId = UUID.randomUUID();
        EventTask task = EventTask.builder()
                .id(taskId)
                .title("Old Title")
                .description("Old Desc")
                .points(50)
                .build();
        UpdateEventTaskDto updateDto = UpdateEventTaskDto.builder()
                .title("New Title")
                .points(200)
                .build();
        EventTaskDto dto = EventTaskDto.builder()
                .id(taskId)
                .title("New Title")
                .points(200)
                .build();

        when(eventTaskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(eventTaskRepository.save(task)).thenReturn(task);
        when(eventTaskMapper.toDto(task)).thenReturn(dto);

        EventTaskDto result = eventTaskService.updateEventTask(taskId, updateDto);

        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        assertEquals(200, result.getPoints());
    }

    @Test
    void deleteEventTask_CallsRepositoryDeleteById() {
        UUID taskId = UUID.randomUUID();
        when(eventTaskRepository.existsById(taskId)).thenReturn(true);

        eventTaskService.deleteEventTask(taskId);

        verify(eventTaskRepository).deleteById(taskId);
    }

    @Test
    void deleteEventTask_ThrowsResourceNotFoundException_WhenNotFound() {
        UUID taskId = UUID.randomUUID();
        when(eventTaskRepository.existsById(taskId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> eventTaskService.deleteEventTask(taskId));
    }

    @Test
    void getAllEventTasks_ReturnsEmptyList() {
        when(eventTaskRepository.findAll()).thenReturn(Collections.emptyList());

        List<EventTaskDto> result = eventTaskService.getAllEventTasks();

        assertTrue(result.isEmpty());
    }
}
