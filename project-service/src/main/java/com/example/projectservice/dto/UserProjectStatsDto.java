package com.example.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProjectStatsDto {
    private int activeCount;
    private int completedCount;
    private int frozenCount;
    private int totalCount;
}
