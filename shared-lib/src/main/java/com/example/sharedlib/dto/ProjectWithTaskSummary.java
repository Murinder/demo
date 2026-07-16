package com.example.sharedlib.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectWithTaskSummary {
    private UUID id;
    private String title;
    private String description;
    private String projectType;
    private UUID createdBy;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private int memberCount;
    private int tasksDone;
    private int tasksTotal;
    private int tasksReview;
    private int tasksBlocked;
    private OffsetDateTime lastTaskUpdate;
    private List<MemberTaskSummary> members;
}
