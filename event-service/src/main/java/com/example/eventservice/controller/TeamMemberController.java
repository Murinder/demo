package com.example.eventservice.controller;

import com.example.eventservice.dto.CreateTeamMemberDto;
import com.example.eventservice.dto.TeamMemberDto;
import com.example.eventservice.dto.UpdateTeamMemberDto;
import com.example.eventservice.model.TeamMember;
import com.example.eventservice.service.TeamMemberService;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/team-members")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Team Members", description = "Управление участниками команд")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    @Operation(summary = "Get all team members")
    @GetMapping
    public ResponseEntity<List<TeamMemberDto>> getAllTeamMembers() {
        return ResponseEntity.ok(teamMemberService.getAllTeamMembers());
    }

    @Operation(summary = "Get team member by team and user ID")
    @GetMapping("/{teamId}/{userId}")
    public ResponseEntity<TeamMemberDto> getTeamMemberById(@PathVariable UUID teamId, @PathVariable UUID userId) {
        TeamMember.TeamMemberId id = new TeamMember.TeamMemberId(teamId, userId);
        return ResponseEntity.ok(teamMemberService.getTeamMemberById(id));
    }

    @Operation(summary = "Add a member to a team")
    @PostMapping
    public ResponseEntity<TeamMemberDto> createTeamMember(@RequestBody CreateTeamMemberDto createTeamMemberDto) {
        return new ResponseEntity<>(teamMemberService.createTeamMember(createTeamMemberDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a team member")
    @PutMapping("/{teamId}/{userId}")
    public ResponseEntity<TeamMemberDto> updateTeamMember(@PathVariable UUID teamId, @PathVariable UUID userId, @RequestBody UpdateTeamMemberDto updateTeamMemberDto) {
        TeamMember.TeamMemberId id = new TeamMember.TeamMemberId(teamId, userId);
        return ResponseEntity.ok(teamMemberService.updateTeamMember(id, updateTeamMemberDto));
    }

    @Operation(summary = "Remove a member from a team")
    @DeleteMapping("/{teamId}/{userId}")
    public ResponseEntity<Void> deleteTeamMember(@PathVariable UUID teamId, @PathVariable UUID userId) {
        TeamMember.TeamMemberId id = new TeamMember.TeamMemberId(teamId, userId);
        teamMemberService.deleteTeamMember(id);
        return ResponseEntity.noContent().build();
    }
}