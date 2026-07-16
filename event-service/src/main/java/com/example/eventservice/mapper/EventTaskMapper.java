package com.example.eventservice.mapper;

import com.example.eventservice.dto.CreateEventTaskDto;
import com.example.eventservice.dto.EventTaskDto;
import com.example.eventservice.dto.UpdateEventTaskDto;
import com.example.eventservice.model.EventTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface EventTaskMapper {
    @Mapping(source = "event.id", target = "eventId")
    EventTaskDto toDto(EventTask eventTask);

    @Mapping(target = "event", ignore = true)
    @Mapping(target = "id", ignore = true)
    EventTask toEntity(CreateEventTaskDto createEventTaskDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", ignore = true)
    void updateEntity(UpdateEventTaskDto updateEventTaskDto, @MappingTarget EventTask eventTask);

}