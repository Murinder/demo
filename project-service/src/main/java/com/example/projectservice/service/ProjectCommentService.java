package com.example.projectservice.service;

import com.example.projectservice.dto.ProjectCommentDto;
import com.example.projectservice.model.ProjectComment;
import com.example.projectservice.repository.ProjectCommentRepository;
import com.example.projectservice.repository.ProjectRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProjectCommentService {

    private final ProjectCommentRepository commentRepository;
    private final ProjectRepository projectRepository;
    private final UserNameCacheService userNameCacheService;

    public ProjectCommentDto addComment(UUID projectId, UUID authorId, String authorRole, String content) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found: " + projectId);
        }

        String authorName = userNameCacheService.fetchUserName(authorId);
        if (authorName == null || authorName.isBlank()) {
            authorName = "User " + authorId.toString().substring(0, 8);
        }

        ProjectComment comment = ProjectComment.builder()
                .projectId(projectId)
                .authorId(authorId)
                .authorName(authorName)
                .authorRole(authorRole)
                .content(content)
                .build();

        comment = commentRepository.save(comment);
        log.info("Comment added to project {} by user {}", projectId, authorId);

        return toDto(comment);
    }

    @Transactional(readOnly = true)
    public List<ProjectCommentDto> getComments(UUID projectId) {
        return commentRepository.findByProjectIdOrderByCreatedAtAsc(projectId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ProjectCommentDto toDto(ProjectComment c) {
        return ProjectCommentDto.builder()
                .id(c.getId().toString())
                .projectId(c.getProjectId().toString())
                .authorId(c.getAuthorId().toString())
                .authorName(c.getAuthorName())
                .authorRole(c.getAuthorRole())
                .content(c.getContent())
                .createdAt(c.getCreatedAt().toString())
                .build();
    }
}
