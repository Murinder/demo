package com.example.eventservice.mapper;

import com.example.eventservice.dto.CreateTeamMemberDto;
import com.example.eventservice.dto.TeamMemberDto;
import com.example.eventservice.dto.UpdateTeamMemberDto;
import com.example.eventservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TeamMemberMapper {
    @Mapping(source = "id.teamId", target = "teamId")
    @Mapping(source = "id.userId", target = "userId")
    TeamMemberDto toDto(TeamMember teamMember);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "team", ignore = true)
    TeamMember toEntity(CreateTeamMemberDto createTeamMemberDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "team", ignore = true)
    void updateEntity(UpdateTeamMemberDto updateTeamMemberDto, @MappingTarget TeamMember teamMember);
}