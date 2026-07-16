package com.example.coreservice.service;

import com.example.coreservice.dto.FacultyDto;
import com.example.coreservice.model.entity.Faculty;
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
public class FacultyService {

    private final FacultyRepository facultyRepository;

    @Transactional(readOnly = true)
    public List<FacultyDto> getAllFaculties() {
        return facultyRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FacultyDto getFacultyById(UUID id) {
        return facultyRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found: " + id));
    }

    public FacultyDto createFaculty(FacultyDto dto) {
        if (dto.getCode() != null && facultyRepository.existsByCode(dto.getCode())) {
            throw new ValidationException("Faculty with code '" + dto.getCode() + "' already exists");
        }
        Faculty faculty = Faculty.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .description(dto.getDescription())
                .build();
        return mapToDto(facultyRepository.save(faculty));
    }

    public FacultyDto updateFaculty(UUID id, FacultyDto dto) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found: " + id));
        faculty.setName(dto.getName());
        faculty.setCode(dto.getCode());
        faculty.setDescription(dto.getDescription());
        faculty.setUpdatedAt(OffsetDateTime.now());
        return mapToDto(facultyRepository.save(faculty));
    }

    public void deleteFaculty(UUID id) {
        if (!facultyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Faculty not found: " + id);
        }
        facultyRepository.deleteById(id);
    }

    private FacultyDto mapToDto(Faculty faculty) {
        return FacultyDto.builder()
                .id(faculty.getId())
                .name(faculty.getName())
                .code(faculty.getCode())
                .description(faculty.getDescription())
                .createdAt(faculty.getCreatedAt())
                .updatedAt(faculty.getUpdatedAt())
                .build();
    }
}
