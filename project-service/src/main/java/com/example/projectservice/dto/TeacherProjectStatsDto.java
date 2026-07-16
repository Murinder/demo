package com.example.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherProjectStatsDto {
    private UUID userId;
    private int activeCount;
    private int completedCount;
    private int totalCount;
    private int totalStudents;
    private int activeStudents;
}
