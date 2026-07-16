package com.example.eventservice.service;

import com.example.eventservice.dto.CreateTeamMemberDto;
import com.example.eventservice.dto.TeamMemberDto;
import com.example.eventservice.dto.UpdateTeamMemberDto;
import com.example.eventservice.model.TeamMember;

import java.util.List;

public interface TeamMemberService {
    List<TeamMemberDto> getAllTeamMembers();

    TeamMemberDto getTeamMemberById(TeamMember.TeamMemberId id);

    TeamMemberDto createTeamMember(CreateTeamMemberDto createTeamMemberDto);

    TeamMemberDto updateTeamMember(TeamMember.TeamMemberId id, UpdateTeamMemberDto updateTeamMemberDto);

    void deleteTeamMember(TeamMember.TeamMemberId id);
}