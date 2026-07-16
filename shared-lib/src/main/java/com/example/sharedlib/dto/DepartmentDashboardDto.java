package com.example.sharedlib.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentDashboardDto {
    private int totalProjects;
    private int activeProjects;
    private int completedProjects;
    private List<ProjectWithTaskSummary> projects;
    private Map<String, Integer> projectsByMonth;
}
