package com.example.ratingservice.service;

import com.example.ratingservice.dto.DepartmentRatingDto;
import com.example.ratingservice.model.DepartmentRating;
import com.example.ratingservice.repository.DepartmentRatingRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentRatingService {

    private final DepartmentRatingRepository departmentRatingRepository;

    public DepartmentRatingDto getDepartmentRating(UUID departmentId) {
        DepartmentRating rating = departmentRatingRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department rating not found: " + departmentId));
        return toDto(rating);
    }

    public List<DepartmentRatingDto> getDepartmentRanking() {
        return departmentRatingRepository.findAllRanked().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private DepartmentRatingDto toDto(DepartmentRating r) {
        return DepartmentRatingDto.builder()
                .departmentId(r.getDepartmentId())
                .totalScore(r.getTotalScore())
                .updatedAt(r.getUpdatedAt())
                .semester(r.getSemester())
                .calculationDetails(r.getCalculationDetails())
                .facultyId(r.getFacultyId())
                .build();
    }
}
