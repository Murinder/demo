package com.example.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTaskStatsDto {
    private int todoCount;
    private int inProgressCount;
    private int reviewCount;
    private int doneCount;
    private int blockedCount;
    private int totalCount;
}
