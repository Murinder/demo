package com.example.projectservice.service;

import com.example.projectservice.model.Project;
import com.example.projectservice.model.ProjectMember;
import com.example.projectservice.repository.ProjectMemberRepository;
import com.example.sharedlib.dto.ProjectMemberDto;
import com.example.sharedlib.enums.ProjectRole;
import com.example.sharedlib.enums.ProjectStatus;
import com.example.sharedlib.event.EventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectMemberServiceTest {

    @Mock
    private ProjectMemberRepository memberRepository;
    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private ProjectMemberService projectMemberService;

    @Test
    void addMember_SavesMemberAndPublishesEvent() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Project project = Project.builder()
                .id(projectId)
                .title("Test Project")
                .status(ProjectStatus.ACTIVE)
                .createdBy(UUID.randomUUID())
                .build();

        ProjectMember.ProjectMemberId memberId = new ProjectMember.ProjectMemberId(projectId, userId);
        ProjectMember savedMember = ProjectMember.builder()
                .id(memberId)
                .project(project)
                .role(ProjectRole.MEMBER)
                .build();

        when(memberRepository.existsById(memberId)).thenReturn(false);
        when(memberRepository.save(any(ProjectMember.class))).thenReturn(savedMember);

        ProjectMemberDto result = projectMemberService.addMember(project, userId, ProjectRole.MEMBER);

        assertNotNull(result);
        assertEquals(projectId, result.getProjectId());
        assertEquals(userId, result.getUserId());
        assertEquals(ProjectRole.MEMBER, result.getRole());
        verify(eventPublisher).publish(eq("etsopy.project"), eq("project.member_added"), any());
    }

    @Test
    void addMember_WhenAlreadyExists_ThrowsException() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Project project = Project.builder()
                .id(projectId)
                .title("Test Project")
                .status(ProjectStatus.ACTIVE)
                .createdBy(UUID.randomUUID())
                .build();

        ProjectMember.ProjectMemberId memberId = new ProjectMember.ProjectMemberId(projectId, userId);
        when(memberRepository.existsById(memberId)).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> projectMemberService.addMember(project, userId, ProjectRole.MEMBER));
        verify(memberRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any(), any(), any());
    }

    @Test
    void removeMember_DeletesAndPublishesEvent() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        projectMemberService.removeMember(projectId, userId);

        verify(memberRepository).deleteById(new ProjectMember.ProjectMemberId(projectId, userId));
        verify(eventPublisher).publish(eq("etsopy.project"), eq("project.member_removed"), any());
    }

    @Test
    void getProjectMembers_ReturnsList() {
        UUID projectId = UUID.randomUUID();
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        Project project = Project.builder().id(projectId).title("P").status(ProjectStatus.ACTIVE).createdBy(userId1).build();

        ProjectMember member1 = ProjectMember.builder()
                .id(new ProjectMember.ProjectMemberId(projectId, userId1))
                .project(project)
                .role(ProjectRole.LEADER)
                .build();
        ProjectMember member2 = ProjectMember.builder()
                .id(new ProjectMember.ProjectMemberId(projectId, userId2))
                .project(project)
                .role(ProjectRole.MEMBER)
                .build();

        when(memberRepository.findById_ProjectId(projectId)).thenReturn(List.of(member1, member2));

        List<ProjectMemberDto> result = projectMemberService.getProjectMembers(projectId);

        assertEquals(2, result.size());
    }

    @Test
    void findProjectsByUserId_ReturnsMemberList() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        Project project = Project.builder().id(projectId).title("P").status(ProjectStatus.ACTIVE).createdBy(userId).build();
        ProjectMember member = ProjectMember.builder()
                .id(new ProjectMember.ProjectMemberId(projectId, userId))
                .project(project)
                .role(ProjectRole.LEADER)
                .build();

        when(memberRepository.findById_UserId(userId)).thenReturn(List.of(member));

        List<ProjectMember> result = projectMemberService.findProjectsByUserId(userId);

        assertEquals(1, result.size());
        assertEquals(projectId, result.get(0).getId().getProjectId());
    }
}
