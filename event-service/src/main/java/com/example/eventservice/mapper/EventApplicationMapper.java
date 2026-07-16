package com.example.eventservice.mapper;

import com.example.eventservice.dto.CreateEventApplicationDto;
import com.example.eventservice.dto.EventApplicationDto;
import com.example.eventservice.dto.UpdateEventApplicationDto;
import com.example.eventservice.model.EventApplication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface EventApplicationMapper {
    @Mapping(source = "event.id", target = "eventId")
    @Mapping(target = "userName", ignore = true)
    EventApplicationDto toDto(EventApplication eventApplication);

    @Mapping(source = "eventId", target = "event.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "teamId", ignore = true)
    EventApplication toEntity(CreateEventApplicationDto createEventApplicationDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "teamId", ignore = true)
    @Mapping(target = "motivation", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "participantRole", ignore = true)
    @Mapping(target = "presentationTitle", ignore = true)
    @Mapping(target = "presentationDescription", ignore = true)
    void updateEntity(UpdateEventApplicationDto updateEventApplicationDto, @MappingTarget EventApplication eventApplication);

}