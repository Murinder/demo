package com.example.ratingservice.client;

import com.example.sharedlib.dto.ProjectMemberDto;
import com.example.sharedlib.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "project-service")
public interface ProjectServiceClient {

    @GetMapping("/api/v1/projects/{projectId}/members")
    ApiResponse<List<ProjectMemberDto>> getProjectMembers(@PathVariable UUID projectId);
}
