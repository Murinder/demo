package com.example.ratingservice.client;

import com.example.sharedlib.dto.UserProfileDto;
import com.example.sharedlib.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "core-service")
public interface CoreServiceClient {

    @GetMapping("/api/v1/auth/profile/{userId}")
    ApiResponse<UserProfileDto> getUserProfile(@PathVariable UUID userId);

    @GetMapping("/api/v1/users/students/ids-by-group")
    ApiResponse<List<UUID>> getStudentIdsByGroup(@RequestParam String groupName);

    @GetMapping("/api/v1/users/students/ids-by-department")
    ApiResponse<List<UUID>> getStudentIdsByDepartment(@RequestParam UUID departmentId);

    @GetMapping("/api/v1/users/students/ids-by-faculty")
    ApiResponse<List<UUID>> getStudentIdsByFaculty(@RequestParam UUID facultyId);
}
