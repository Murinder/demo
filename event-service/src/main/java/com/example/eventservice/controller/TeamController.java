package com.example.eventservice.controller;

import com.example.eventservice.dto.CreateTeamDto;
import com.example.eventservice.dto.TeamDto;
import com.example.eventservice.dto.UpdateTeamDto;
import com.example.eventservice.service.TeamService;
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
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Teams", description = "Управление командами")
public class TeamController {

    private final TeamService teamService;

    @Operation(summary = "Get all teams")
    @GetMapping
    public ResponseEntity<List<TeamDto>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @Operation(summary = "Get team by ID")
    @GetMapping("/{id}")
    public ResponseEntity<TeamDto> getTeamById(@PathVariable UUID id) {
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    @Operation(summary = "Create a new team")
    @PostMapping
    public ResponseEntity<TeamDto> createTeam(@RequestBody CreateTeamDto createDto) {
        return new ResponseEntity<>(teamService.createTeam(createDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a team")
    @PutMapping("/{id}")
    public ResponseEntity<TeamDto> updateTeam(@PathVariable UUID id, @RequestBody UpdateTeamDto updateDto) {
        return ResponseEntity.ok(teamService.updateTeam(id, updateDto));
    }

    @Operation(summary = "Delete a team")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable UUID id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }
}