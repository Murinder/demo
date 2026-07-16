package com.example.coreservice.controller;

import com.example.coreservice.dto.FacultyDto;
import com.example.coreservice.service.FacultyService;
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
@RequestMapping("/api/v1/faculties")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Faculties", description = "Faculty management APIs")
public class FacultyController {

    private final FacultyService facultyService;

    @GetMapping
    @Operation(summary = "Get all faculties")
    public ResponseEntity<List<FacultyDto>> getAll() {
        return ResponseEntity.ok(facultyService.getAllFaculties());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get faculty by id")
    public ResponseEntity<FacultyDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(facultyService.getFacultyById(id));
    }

    @AdminOnly
    @PostMapping
    @Operation(summary = "Create a new faculty")
    public ResponseEntity<FacultyDto> create(@RequestBody FacultyDto facultyDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facultyService.createFaculty(facultyDto));
    }

    @AdminOnly
    @PutMapping("/{id}")
    @Operation(summary = "Update a faculty")
    public ResponseEntity<FacultyDto> update(@PathVariable UUID id, @RequestBody FacultyDto facultyDto) {
        return ResponseEntity.ok(facultyService.updateFaculty(id, facultyDto));
    }

    @AdminOnly
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a faculty")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.noContent().build();
    }
}
