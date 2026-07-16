package com.example.coreservice.service;

import com.example.coreservice.dto.DepartmentDto;
import com.example.coreservice.model.entity.Department;
import com.example.coreservice.model.entity.Faculty;
import com.example.coreservice.repository.DepartmentRepository;
import com.example.coreservice.repository.FacultyRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import com.example.sharedlib.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;

    @Transactional(readOnly = true)
    public List<DepartmentDto> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DepartmentDto getDepartmentById(UUID id) {
        return departmentRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<DepartmentDto> getDepartmentsByFacultyId(UUID facultyId) {
        return departmentRepository.findByFacultyId(facultyId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public DepartmentDto createDepartment(DepartmentDto dto) {
        if (dto.getCode() != null && departmentRepository.existsByCode(dto.getCode())) {
            throw new ValidationException("Department with code '" + dto.getCode() + "' already exists");
        }
        Faculty faculty = facultyRepository.findById(dto.getFacultyId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found: " + dto.getFacultyId()));

        Department department = Department.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .faculty(faculty)
                .headUserId(dto.getHeadUserId())
                .description(dto.getDescription())
                .build();
        return mapToDto(departmentRepository.save(department));
    }

    public DepartmentDto updateDepartment(UUID id, DepartmentDto dto) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));

        if (dto.getFacultyId() != null && !dto.getFacultyId().equals(department.getFaculty().getId())) {
            Faculty faculty = facultyRepository.findById(dto.getFacultyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Faculty not found: " + dto.getFacultyId()));
            department.setFaculty(faculty);
        }

        department.setName(dto.getName());
        department.setCode(dto.getCode());
        department.setHeadUserId(dto.getHeadUserId());
        department.setDescription(dto.getDescription());
        department.setUpdatedAt(OffsetDateTime.now());
        return mapToDto(departmentRepository.save(department));
    }

    public void deleteDepartment(UUID id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department not found: " + id);
        }
        departmentRepository.deleteById(id);
    }

    private DepartmentDto mapToDto(Department department) {
        return DepartmentDto.builder()
                .id(department.getId())
                .name(department.getName())
                .code(department.getCode())
                .facultyId(department.getFaculty().getId())
                .facultyName(department.getFaculty().getName())
                .headUserId(department.getHeadUserId())
                .description(department.getDescription())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .build();
    }
}
