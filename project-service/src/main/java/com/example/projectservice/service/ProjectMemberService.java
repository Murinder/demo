package com.example.projectservice.service;

import com.example.projectservice.client.UserServiceClient;
import com.example.projectservice.model.Project;
import com.example.projectservice.model.ProjectMember;
import com.example.projectservice.repository.ProjectMemberRepository;
import com.example.sharedlib.config.RabbitMqAutoConfiguration;
import com.example.sharedlib.dto.ProjectMemberDto;
import com.example.sharedlib.enums.ProjectRole;
import com.example.sharedlib.event.EventPublisher;
import com.example.sharedlib.event.ProjectEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProjectMemberService {

    private final ProjectMemberRepository memberRepository;
    private final EventPublisher eventPublisher;
    private final UserServiceClient userServiceClient;

    public ProjectMemberDto addMember(Project project, UUID userId, ProjectRole role) {
        ProjectMember.ProjectMemberId memberId = new ProjectMember.ProjectMemberId(project.getId(), userId);
        if (memberRepository.existsById(memberId)) {
            throw new IllegalStateException("Member already exists in the project");
        }
        ProjectMember newMember = ProjectMember.builder()
                .id(memberId)
                .project(project)
                .role(role)
                .build();
        ProjectMember saved = memberRepository.save(newMember);

        // Publish member_added event
        ProjectEvent event = ProjectEvent.builder()
                .projectId(project.getId())
                .userId(userId)
                .role(role.name())
                .title(project.getTitle())
                .build();
        event.init("project-service");
        eventPublisher.publish(RabbitMqAutoConfiguration.PROJECT_EXCHANGE, "project.member_added", event);

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ProjectMemberDto> getProjectMembers(UUID projectId) {
        List<ProjectMember> members = memberRepository.findById_ProjectId(projectId);
        return members.stream()
                .map(m -> {
                    ProjectMemberDto dto = toDto(m);
                    dto.setUserName(resolveUserName(m.getId().getUserId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void removeMember(UUID projectId, UUID userId) {
        memberRepository.deleteById(new ProjectMember.ProjectMemberId(projectId, userId));

        // Publish member_removed event
        ProjectEvent event = ProjectEvent.builder()
                .projectId(projectId)
                .userId(userId)
                .build();
        event.init("project-service");
        eventPublisher.publish(RabbitMqAutoConfiguration.PROJECT_EXCHANGE, "project.member_removed", event);
    }

    @Transactional(readOnly = true)
    public List<ProjectMember> findProjectsByUserId(UUID userId) {
        return memberRepository.findById_UserId(userId);
    }

    @SuppressWarnings("unchecked")
    private String resolveUserName(UUID userId) {
        try {
            Map<String, Object> response = userServiceClient.getUserProfileRaw(userId);
            Object data = response.get("data");
            if (data instanceof Map) {
                Map<String, Object> profile = (Map<String, Object>) data;
                String first = (String) profile.getOrDefault("firstName", "");
                String last = (String) profile.getOrDefault("lastName", "");
                String name = (first + " " + last).trim();
                if (!name.isEmpty()) return name;
            }
        } catch (Exception e) {
            log.warn("Failed to resolve userName for {}: {}", userId, e.getMessage());
        }
        return null;
    }

    private ProjectMemberDto toDto(ProjectMember member) {
        return ProjectMemberDto.builder()
                .projectId(member.getId().getProjectId())
                .userId(member.getId().getUserId())
                .role(member.getRole())
                .build();
    }
}