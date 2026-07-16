package com.example.eventservice.service.impl;

import com.example.eventservice.dto.CreateTeamMemberDto;
import com.example.eventservice.dto.TeamMemberDto;
import com.example.eventservice.dto.UpdateTeamMemberDto;
import com.example.eventservice.exception.ResourceNotFoundException;
import com.example.eventservice.mapper.TeamMemberMapper;
import com.example.eventservice.model.Team;
import com.example.eventservice.model.TeamMember;
import com.example.eventservice.repository.EventApplicationRepository;
import com.example.eventservice.repository.TeamMemberRepository;
import com.example.eventservice.repository.TeamRepository;
import com.example.eventservice.service.TeamMemberService;
import com.example.sharedlib.config.RabbitMqAutoConfiguration;
import com.example.sharedlib.exception.ForbiddenException;
import com.example.sharedlib.exception.ValidationException;
import com.example.sharedlib.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberMapper teamMemberMapper;
    private final EventApplicationRepository applicationRepository;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public List<TeamMemberDto> getAllTeamMembers() {
        return teamMemberRepository.findAll().stream()
                .map(teamMemberMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TeamMemberDto getTeamMemberById(TeamMember.TeamMemberId id) {
        return teamMemberRepository.findById(id)
                .map(teamMemberMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("TeamMember not found with id: " + id));
    }

    @Override
    public TeamMemberDto createTeamMember(CreateTeamMemberDto createTeamMemberDto) {
        Team team = teamRepository.findById(createTeamMemberDto.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + createTeamMemberDto.getTeamId()));

        UUID eventId = team.getEvent().getId();
        if (teamMemberRepository.existsByUserIdAndEventId(createTeamMemberDto.getUserId(), eventId)) {
            throw new ValidationException("Вы уже состоите в команде для этого мероприятия");
        }

        TeamMember teamMember = teamMemberMapper.toEntity(createTeamMemberDto);
        TeamMember.TeamMemberId memberId = new TeamMember.TeamMemberId(
                createTeamMemberDto.getTeamId(),
                createTeamMemberDto.getUserId()
        );
        teamMember.setId(memberId);
        teamMember.setTeam(team);
        return teamMemberMapper.toDto(teamMemberRepository.save(teamMember));
    }

    @Override
    public TeamMemberDto updateTeamMember(TeamMember.TeamMemberId id, UpdateTeamMemberDto updateTeamMemberDto) {
        TeamMember teamMember = teamMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TeamMember not found with id: " + id));
        teamMemberMapper.updateEntity(updateTeamMemberDto, teamMember);
        return teamMemberMapper.toDto(teamMemberRepository.save(teamMember));
    }

    @Override
    @Transactional
    public void deleteTeamMember(TeamMember.TeamMemberId id) {
        TeamMember teamMember = teamMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TeamMember not found with id: " + id));

        Team team = teamMember.getTeam();
        UUID targetUserId = id.getUserId();
        UUID callerId = UserContext.getCurrentUserId();

        // Нельзя удалить лидера команды
        if (targetUserId.equals(team.getCreatedBy())) {
            throw new ValidationException("Лидер не может быть удалён из команды");
        }

        // Только лидер может исключать участников (или участник сам себя)
        if (!callerId.equals(team.getCreatedBy()) && !callerId.equals(targetUserId)) {
            throw new ForbiddenException("Только лидер команды может исключать участников");
        }

        teamMemberRepository.delete(teamMember);

        // Каскад: удалить заявку (чтобы студент мог перерегистрироваться)
        UUID eventId = team.getEvent().getId();
        applicationRepository.findByEvent_IdAndUserIdAndTeamId(eventId, targetUserId, team.getId())
                .ifPresent(applicationRepository::delete);

        // Нотификация исключённому участнику
        String eventTitle = team.getEvent().getTitle();
        String teamName = team.getName();
        rabbitTemplate.convertAndSend(
                RabbitMqAutoConfiguration.NOTIFICATION_EXCHANGE,
                RabbitMqAutoConfiguration.NOTIFICATION_SEND_KEY,
                Map.of(
                        "userId", targetUserId.toString(),
                        "title", "Исключение из команды",
                        "message", "Вы были исключены из команды \"" + teamName + "\" мероприятия \"" + eventTitle + "\". Вы можете зарегистрироваться заново.",
                        "type", "EVENT_UPDATE"
                )
        );
    }
}
