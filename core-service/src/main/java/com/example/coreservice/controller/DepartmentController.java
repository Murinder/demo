package com.example.coreservice.controller;

import com.example.coreservice.dto.DepartmentDto;
import com.example.coreservice.service.DepartmentService;
import com.example.sharedlib.security.AdminOnly;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Departments", description = "Department management APIs")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    @Operation(summary = "Get all departments")
    public ResponseEntity<List<DepartmentDto>> getAll() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by id")
    public ResponseEntity<DepartmentDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @GetMapping("/faculty/{facultyId}")
    @Operation(summary = "Get departments by faculty")
    public ResponseEntity<List<DepartmentDto>> getByFaculty(@PathVariable UUID facultyId) {
        return ResponseEntity.ok(departmentService.getDepartmentsByFacultyId(facultyId));
    }

    @AdminOnly
    @PostMapping
    @Operation(summary = "Create a new department")
    public ResponseEntity<DepartmentDto> create(@RequestBody DepartmentDto departmentDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.createDepartment(departmentDto));
    }

    @AdminOnly
    @PutMapping("/{id}")
    @Operation(summary = "Update a department")
    public ResponseEntity<DepartmentDto> update(@PathVariable UUID id, @RequestBody DepartmentDto departmentDto) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, departmentDto));
    }

    @AdminOnly
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a department")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}
