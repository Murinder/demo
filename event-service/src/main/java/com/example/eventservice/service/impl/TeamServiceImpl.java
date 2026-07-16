package com.example.eventservice.service.impl;

import com.example.eventservice.exception.ResourceNotFoundException;
import com.example.eventservice.dto.CreateTeamDto;
import com.example.eventservice.dto.TeamDto;
import com.example.eventservice.dto.TeamMemberDetailDto;
import com.example.eventservice.dto.UpdateTeamDto;
import com.example.eventservice.mapper.TeamMapper;
import com.example.eventservice.model.Event;
import com.example.eventservice.model.Team;
import com.example.eventservice.model.TeamMember;
import com.example.eventservice.repository.EventRepository;
import com.example.eventservice.repository.TeamMemberRepository;
import com.example.eventservice.repository.TeamRepository;
import com.example.eventservice.service.TeamService;
import com.example.eventservice.service.UserNameCacheService;
import com.example.sharedlib.config.RabbitMqAutoConfiguration;
import com.example.sharedlib.event.EventEvent;
import com.example.sharedlib.event.EventPublisher;
import com.example.sharedlib.exception.ValidationException;
import com.example.sharedlib.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final EventRepository eventRepository;
    private final TeamMapper teamMapper;
    private final EventPublisher eventPublisher;
    private final UserNameCacheService userNameCacheService;

    @Override
    @Transactional(readOnly = true)
    public List<TeamDto> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(this::enrichTeamDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TeamDto getTeamById(UUID id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        return enrichTeamDto(team);
    }

    @Override
    @Transactional
    public TeamDto createTeam(CreateTeamDto createDto) {
        Event event = eventRepository.findById(createDto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        UUID creatorId = UserContext.getCurrentUserId();

        if (teamMemberRepository.existsByUserIdAndEventId(creatorId, event.getId())) {
            throw new ValidationException("Вы уже состоите в команде для этого мероприятия");
        }
        if (teamRepository.existsByEvent_IdAndCreatedBy(event.getId(), creatorId)) {
            throw new ValidationException("Вы уже создали команду для этого мероприятия");
        }

        Team team = teamMapper.toEntity(createDto);
        team.setEvent(event);
        team.setCreatedBy(creatorId);
        team = teamRepository.save(team);

        TeamMember.TeamMemberId memberId = new TeamMember.TeamMemberId(team.getId(), creatorId);
        TeamMember leaderMember = TeamMember.builder()
                .id(memberId)
                .team(team)
                .role("leader")
                .build();
        teamMemberRepository.save(leaderMember);

        EventEvent domainEvent = EventEvent.builder()
                .domainEventId(event.getId())
                .teamId(team.getId())
                .build();
        domainEvent.init("event-service");
        eventPublisher.publish(RabbitMqAutoConfiguration.EVENT_EXCHANGE, "event.team_formed", domainEvent);

        return teamMapper.toDto(team);
    }

    @Override
    @Transactional
    public TeamDto updateTeam(UUID id, UpdateTeamDto updateDto) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        teamMapper.updateEntity(updateDto, team);
        team = teamRepository.save(team);
        return teamMapper.toDto(team);
    }

    @Override
    @Transactional
    public void deleteTeam(UUID id) {
        teamRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamDto> getTeamsByEventId(UUID eventId) {
        return teamRepository.findByEventIdWithMembers(eventId).stream()
                .map(this::enrichTeamDto)
                .collect(Collectors.toList());
    }

    private TeamDto enrichTeamDto(Team team) {
        TeamDto dto = teamMapper.toDto(team);
        if (team.getMembers() != null) {
            dto.setMembers(team.getMembers().stream().map(m -> {
                String name = userNameCacheService.fetchUserName(m.getId().getUserId());
                return TeamMemberDetailDto.builder()
                        .userId(m.getId().getUserId())
                        .userName(name)
                        .role(m.getRole())
                        .joinedAt(m.getJoinedAt())
                        .build();
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}