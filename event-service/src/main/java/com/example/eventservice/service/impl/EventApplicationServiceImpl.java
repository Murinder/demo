package com.example.eventservice.service.impl;

import com.example.eventservice.exception.ResourceNotFoundException;
import com.example.eventservice.dto.CreateEventApplicationDto;
import com.example.eventservice.dto.EventApplicationDto;
import com.example.eventservice.dto.UpdateEventApplicationDto;
import com.example.eventservice.mapper.EventApplicationMapper;
import com.example.eventservice.model.Event;
import com.example.eventservice.model.EventApplication;
import com.example.eventservice.model.Team;
import com.example.eventservice.model.TeamMember;
import com.example.eventservice.model.enums.ApplicationStatus;
import com.example.eventservice.repository.EventApplicationRepository;
import com.example.eventservice.repository.EventRepository;
import com.example.eventservice.repository.TeamMemberRepository;
import com.example.eventservice.repository.TeamRepository;
import com.example.eventservice.service.EventApplicationService;
import com.example.eventservice.service.UserNameCacheService;
import com.example.sharedlib.config.RabbitMqAutoConfiguration;
import com.example.sharedlib.event.EventEvent;
import com.example.sharedlib.event.EventPublisher;
import com.example.sharedlib.exception.ForbiddenException;
import com.example.sharedlib.exception.ValidationException;
import com.example.sharedlib.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventApplicationServiceImpl implements EventApplicationService {

    private final EventApplicationRepository applicationRepository;
    private final EventRepository eventRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final EventApplicationMapper applicationMapper;
    private final EventPublisher eventPublisher;
    private final UserNameCacheService userNameCacheService;

    @Override
    @Transactional(readOnly = true)
    public List<EventApplicationDto> getAllApplications() {
        return applicationRepository.findAll().stream()
                .map(this::toDtoWithUserName)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventApplicationDto getApplicationById(UUID id) {
        EventApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        return toDtoWithUserName(application);
    }

    @Override
    @Transactional
    public EventApplicationDto createApplication(CreateEventApplicationDto createDto) {
        Event event = eventRepository.findById(createDto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (applicationRepository.findByEvent_IdAndUserId(createDto.getEventId(), createDto.getUserId()).isPresent()) {
            throw new ValidationException("Вы уже подали заявку на это мероприятие");
        }

        EventApplication application = applicationMapper.toEntity(createDto);
        application.setEvent(event);

        String role = createDto.getParticipantRole();

        if ("OBSERVER".equals(role)) {
            application.setStatus(ApplicationStatus.APPROVED);
            application = applicationRepository.save(application);
            publishEvent(event.getId(), application, "event.application_auto_approved");
        } else if ("PARTICIPANT".equals(role)) {
            if (createDto.getPresentationTitle() == null || createDto.getPresentationTitle().isBlank()) {
                throw new ValidationException("Название выступления обязательно для участника");
            }
            application.setStatus(ApplicationStatus.SUBMITTED);
            application = applicationRepository.save(application);
            publishEvent(event.getId(), application, "event.application_submitted");
        } else if ("TEAM_CREATOR".equals(role)) {
            if (createDto.getTeamName() == null || createDto.getTeamName().isBlank()) {
                throw new ValidationException("Название команды обязательно");
            }
            if (teamMemberRepository.existsByUserIdAndEventId(createDto.getUserId(), event.getId())) {
                throw new ValidationException("Вы уже состоите в команде для этого мероприятия");
            }
            if (teamRepository.existsByEvent_IdAndCreatedBy(event.getId(), createDto.getUserId())) {
                throw new ValidationException("Вы уже создали команду для этого мероприятия");
            }

            Team team = Team.builder()
                    .event(event)
                    .name(createDto.getTeamName().trim())
                    .createdBy(createDto.getUserId())
                    .build();
            team = teamRepository.save(team);

            TeamMember.TeamMemberId memberId = new TeamMember.TeamMemberId(team.getId(), createDto.getUserId());
            TeamMember leaderMember = TeamMember.builder()
                    .id(memberId)
                    .team(team)
                    .role("leader")
                    .build();
            teamMemberRepository.save(leaderMember);

            application.setTeamId(team.getId());
            application.setStatus(ApplicationStatus.SUBMITTED);
            application = applicationRepository.save(application);

            publishEvent(event.getId(), application, "event.application_submitted");
            EventEvent teamEvent = EventEvent.builder()
                    .domainEventId(event.getId())
                    .teamId(team.getId())
                    .build();
            teamEvent.init("event-service");
            eventPublisher.publish(RabbitMqAutoConfiguration.EVENT_EXCHANGE, "event.team_formed", teamEvent);
        } else if ("TEAM_JOINER".equals(role)) {
            if (createDto.getTeamId() == null) {
                throw new ValidationException("Необходимо выбрать команду");
            }
            Team team = teamRepository.findById(createDto.getTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
            if (!team.getEvent().getId().equals(event.getId())) {
                throw new ValidationException("Команда не принадлежит этому мероприятию");
            }
            if (teamMemberRepository.existsByUserIdAndEventId(createDto.getUserId(), event.getId())) {
                throw new ValidationException("Вы уже состоите в команде для этого мероприятия");
            }

            application.setTeamId(team.getId());
            application.setStatus(ApplicationStatus.PENDING_TEAM_APPROVAL);
            application = applicationRepository.save(application);
            publishEvent(event.getId(), application, "event.team_join_requested");
        } else {
            application.setStatus(ApplicationStatus.SUBMITTED);
            application = applicationRepository.save(application);
            publishEvent(event.getId(), application, "event.application_submitted");
        }

        return toDtoWithUserName(application);
    }

    @Override
    @Transactional
    public EventApplicationDto updateApplication(UUID id, UpdateEventApplicationDto updateDto) {
        EventApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        applicationMapper.updateEntity(updateDto, application);
        application.setUpdatedAt(OffsetDateTime.now());
        application = applicationRepository.save(application);

        EventEvent domainEvent = EventEvent.builder()
                .domainEventId(application.getEvent().getId())
                .applicationId(application.getId())
                .applicationStatus(application.getStatus().name())
                .userId(application.getUserId())
                .build();
        domainEvent.init("event-service");
        eventPublisher.publish(RabbitMqAutoConfiguration.EVENT_EXCHANGE, "event.application_decided", domainEvent);

        return toDtoWithUserName(application);
    }

    @Override
    @Transactional
    public void deleteApplication(UUID id) {
        EventApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        if (!UserContext.hasRole("LECTURER") && !UserContext.hasRole("DEPARTMENT_HEAD") && !UserContext.hasRole("ADMIN")) {
            UUID callerId = UserContext.getCurrentUserId();
            if (!application.getUserId().equals(callerId)) {
                throw new ForbiddenException("You can only cancel your own application");
            }
        }
        if ("TEAM_CREATOR".equals(application.getParticipantRole())
                && application.getTeamId() != null) {
            teamRepository.deleteById(application.getTeamId());
        }
        if ("TEAM_JOINER".equals(application.getParticipantRole())
                && application.getTeamId() != null) {
            TeamMember.TeamMemberId memberId = new TeamMember.TeamMemberId(
                    application.getTeamId(), application.getUserId());
            if (teamMemberRepository.existsById(memberId)) {
                teamMemberRepository.deleteById(memberId);
            }
        }
        applicationRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventApplicationDto> getMyApplications() {
        UUID userId = UserContext.getCurrentUserId();
        return applicationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toDtoWithUserName)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventApplicationDto> getApplicationsByEventId(UUID eventId) {
        return applicationRepository.findByEvent_Id(eventId).stream()
                .map(this::toDtoWithUserName)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventApplicationDto teamDecision(UUID id, UpdateEventApplicationDto updateDto) {
        EventApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (application.getStatus() != ApplicationStatus.PENDING_TEAM_APPROVAL) {
            throw new ValidationException("Заявка не ожидает одобрения команды");
        }
        if (application.getTeamId() == null) {
            throw new ValidationException("У заявки нет привязанной команды");
        }

        Team team = teamRepository.findById(application.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        UUID callerId = UserContext.getCurrentUserId();
        if (!team.getCreatedBy().equals(callerId)) {
            throw new ForbiddenException("Только лидер команды может принимать решение по заявке");
        }

        if (updateDto.getStatus() == ApplicationStatus.APPROVED) {
            TeamMember.TeamMemberId memberId = new TeamMember.TeamMemberId(team.getId(), application.getUserId());
            TeamMember member = TeamMember.builder()
                    .id(memberId)
                    .team(team)
                    .role("member")
                    .build();
            teamMemberRepository.save(member);
            application.setStatus(ApplicationStatus.APPROVED);
        } else if (updateDto.getStatus() == ApplicationStatus.REJECTED) {
            application.setStatus(ApplicationStatus.REJECTED);
        } else {
            throw new ValidationException("Допустимые статусы: APPROVED или REJECTED");
        }

        application.setUpdatedAt(OffsetDateTime.now());
        application = applicationRepository.save(application);

        publishEvent(application.getEvent().getId(), application, "event.team_join_decided");

        return toDtoWithUserName(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventApplicationDto> getApplicationsByTeam(UUID eventId, UUID teamId) {
        return applicationRepository.findByEvent_IdAndTeamId(eventId, teamId).stream()
                .map(this::toDtoWithUserName)
                .collect(Collectors.toList());
    }

    private EventApplicationDto toDtoWithUserName(EventApplication application) {
        EventApplicationDto dto = applicationMapper.toDto(application);
        String name = userNameCacheService.fetchUserName(application.getUserId());
        dto.setUserName(name);
        if (application.getTeamId() != null) {
            teamRepository.findById(application.getTeamId())
                    .ifPresent(team -> dto.setTeamName(team.getName()));
        }
        return dto;
    }

    private void publishEvent(UUID eventId, EventApplication application, String routingKey) {
        EventEvent domainEvent = EventEvent.builder()
                .domainEventId(eventId)
                .applicationId(application.getId())
                .applicationStatus(application.getStatus().name())
                .userId(application.getUserId())
                .build();
        domainEvent.init("event-service");
        eventPublisher.publish(RabbitMqAutoConfiguration.EVENT_EXCHANGE, routingKey, domainEvent);
    }
}
