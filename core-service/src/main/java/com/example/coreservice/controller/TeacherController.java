package com.example.coreservice.controller;

import com.example.coreservice.dto.TeacherDetailedDto;
import com.example.coreservice.service.TeacherAggregationService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/teachers")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Teachers", description = "Teacher listing APIs")
public class TeacherController {

    private final UserService userService;
    private final TeacherAggregationService teacherAggregationService;

    @GetMapping
    @Operation(summary = "Get all teachers (lecturers)")
    public ResponseEntity<ApiResponse<List<UserProfileDto>>> getAllTeachers() {
        List<UserProfileDto> teachers = userService.getUsersByRole(UserRole.LECTURER);
        return ResponseEntity.ok(ApiResponse.success(teachers));
    }

    @GetMapping("/detailed")
    @Operation(summary = "Get all teachers with detailed statistics")
    public ResponseEntity<ApiResponse<List<TeacherDetailedDto>>> getAllTeachersDetailed() {
        List<TeacherDetailedDto> teachers = teacherAggregationService.getAllTeachersDetailed();
        return ResponseEntity.ok(ApiResponse.success(teachers));
    }
}
