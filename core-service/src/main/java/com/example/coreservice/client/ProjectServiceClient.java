package com.example.coreservice.client;

import com.example.sharedlib.dto.*;
import com.example.sharedlib.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "project-service")
public interface ProjectServiceClient {

    @GetMapping("/api/v1/projects")
    List<ProjectDto> getAllProjects();

    @GetMapping("/api/v1/projects/user/{userId}")
    ApiResponse<List<ProjectDto>> getProjectsByUserId(@PathVariable UUID userId);

    @GetMapping("/api/v1/projects/user/{userId}/stats")
    ApiResponse<Map<String, Integer>> getUserProjectStats(@PathVariable UUID userId);

    @GetMapping("/api/v1/tasks/user/{userId}/stats")
    ApiResponse<Map<String, Integer>> getUserTaskStats(@PathVariable UUID userId);

    @GetMapping("/api/v1/projects/user/{userId}/detailed-stats")
    ApiResponse<ProjectDashboardStatsDto> getUserProjectDetailedStats(@PathVariable UUID userId);

    @GetMapping("/api/v1/projects/department/{departmentId}/dashboard")
    ApiResponse<DepartmentDashboardDto> getDepartmentDashboard(@PathVariable UUID departmentId);

    @GetMapping("/api/v1/tasks/user/{userId}/recent")
    ApiResponse<List<TaskDto>> getRecentUserTasks(@PathVariable UUID userId);

    @PostMapping("/api/v1/projects/batch/teacher-stats")
    ApiResponse<List<Map<String, Object>>> getBatchTeacherStats(@RequestBody List<UUID> teacherIds);
}
