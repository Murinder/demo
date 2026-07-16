package com.example.coreservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationCommentDto {
    private String id;
    private String applicationId;
    private String authorId;
    private String authorRole;
    private String authorName;
    private String content;
    private String createdAt;
}
