package com.example.eventservice.service;

import com.example.eventservice.dto.CreateTeamDto;
import com.example.eventservice.dto.TeamDto;
import com.example.eventservice.dto.UpdateTeamDto;

import java.util.List;
import java.util.UUID;

public interface TeamService {
    List<TeamDto> getAllTeams();

    TeamDto getTeamById(UUID id);

    TeamDto createTeam(CreateTeamDto createTeamDto);

    TeamDto updateTeam(UUID id, UpdateTeamDto updateTeamDto);

    void deleteTeam(UUID id);

    List<TeamDto> getTeamsByEventId(UUID eventId);
}