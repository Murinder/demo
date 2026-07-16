package com.example.eventservice.mapper;

import com.example.eventservice.dto.CreateTeamDto;
import com.example.eventservice.dto.TeamDto;
import com.example.eventservice.dto.UpdateTeamDto;
import com.example.eventservice.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TeamMapper {
    @Mapping(source = "event.id", target = "eventId")
    @Mapping(target = "memberUserIds", expression = "java(team.getMembers() == null ? java.util.Collections.emptyList() : team.getMembers().stream().map(m -> m.getId().getUserId()).collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "members", ignore = true)
    TeamDto toDto(Team team);

    @Mapping(source = "eventId", target = "event.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "members", ignore = true)
    Team toEntity(CreateTeamDto createTeamDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "members", ignore = true)
    void updateEntity(UpdateTeamDto updateTeamDto, @MappingTarget Team team);
}