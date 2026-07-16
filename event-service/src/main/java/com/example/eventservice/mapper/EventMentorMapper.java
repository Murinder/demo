package com.example.eventservice.mapper;

import com.example.eventservice.dto.CreateEventMentorDto;
import com.example.eventservice.dto.EventMentorDto;
import com.example.eventservice.model.EventMentor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMentorMapper {
    @Mapping(source = "id.eventId", target = "eventId")
    @Mapping(source = "id.userId", target = "userId")
    EventMentorDto toDto(EventMentor eventMentor);

    @Mapping(source = "eventId", target = "id.eventId")
    @Mapping(source = "userId", target = "id.userId")
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "availability", ignore = true)
    @Mapping(target = "expertise", ignore = true)
    EventMentor toEntity(CreateEventMentorDto createEventMentorDto);
}