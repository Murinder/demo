package com.example.projectservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectCommentDto {
    private String id;
    private String projectId;
    private String authorId;
    private String authorName;
    private String authorRole;
    private String content;
    private String createdAt;
}
