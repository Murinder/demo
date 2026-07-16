package com.example.projectservice.config;

import com.example.projectservice.model.ProjectMember;
import com.example.projectservice.repository.ProjectMemberRepository;
import com.example.sharedlib.enums.ProjectRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("projectSecurity")
@RequiredArgsConstructor
public class ProjectSecurity {

    private final ProjectMemberRepository projectMemberRepository;

    public boolean isLeader(UUID projectId, String userId) {
        return projectMemberRepository.findById(new ProjectMember.ProjectMemberId(projectId, UUID.fromString(userId)))
                .map(member -> member.getRole() == ProjectRole.LEADER)
                .orElse(false);
    }

    public boolean isMember(UUID projectId, String userId) {
        return projectMemberRepository.findById(new ProjectMember.ProjectMemberId(projectId, UUID.fromString(userId)))
                .isPresent();
    }
}