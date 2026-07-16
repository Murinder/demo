package com.example.coreservice.controller;

import com.example.coreservice.service.UserService;
import com.example.sharedlib.dto.UserProfileDto;
import com.example.sharedlib.enums.UserRole;
import com.example.sharedlib.response.ApiResponse;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/students")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Students", description = "Student listing APIs")
public class StudentController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all students")
    public ResponseEntity<ApiResponse<List<UserProfileDto>>> getAllStudents() {
        List<UserProfileDto> students = userService.getUsersByRole(UserRole.STUDENT);
        return ResponseEntity.ok(ApiResponse.success(students));
    }

    @GetMapping("/search")
    @Operation(summary = "Search users by name (substring match)")
    public ResponseEntity<ApiResponse<List<UserProfileDto>>> searchByName(
            @RequestParam String q) {
        if (q == null || q.trim().length() < 2) {
            return ResponseEntity.ok(ApiResponse.success(List.of()));
        }
        List<UserProfileDto> results = userService.searchUsersByName(q);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @GetMapping("/groups")
    @Operation(summary = "Get all distinct group names")
    public ResponseEntity<ApiResponse<List<String>>> getAllGroups() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllGroupNames()));
    }

    @GetMapping("/ids-by-group")
    @Operation(summary = "Get student IDs by group name")
    public ResponseEntity<ApiResponse<List<UUID>>> getStudentIdsByGroup(
            @RequestParam String groupName) {
        return ResponseEntity.ok(ApiResponse.success(userService.getStudentIdsByGroup(groupName)));
    }

    @GetMapping("/ids-by-department")
    @Operation(summary = "Get student IDs by department")
    public ResponseEntity<ApiResponse<List<UUID>>> getStudentIdsByDepartment(
            @RequestParam UUID departmentId) {
        return ResponseEntity.ok(ApiResponse.success(userService.getStudentIdsByDepartment(departmentId)));
    }

    @GetMapping("/ids-by-faculty")
    @Operation(summary = "Get student IDs by faculty")
    public ResponseEntity<ApiResponse<List<UUID>>> getStudentIdsByFaculty(
            @RequestParam UUID facultyId) {
        return ResponseEntity.ok(ApiResponse.success(userService.getStudentIdsByFaculty(facultyId)));
    }
}
