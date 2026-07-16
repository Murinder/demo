package com.example.ratingservice.service;

import com.example.ratingservice.dto.RatingCriteriaDto;
import com.example.ratingservice.model.RatingCriteria;
import com.example.ratingservice.model.RatingCriteriaType;
import com.example.ratingservice.repository.RatingCriteriaRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
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
public class RatingCriteriaService {

    private final RatingCriteriaRepository criteriaRepository;

    @Transactional(readOnly = true)
    public List<RatingCriteriaDto> getAllCriteria() {
        return criteriaRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RatingCriteriaDto> getActiveCriteriaByType(RatingCriteriaType type) {
        return criteriaRepository.findByCriteriaTypeAndIsActiveTrue(type).stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    public RatingCriteriaDto createCriteria(RatingCriteriaDto dto) {
        RatingCriteria criteria = RatingCriteria.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .weight(dto.getWeight())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .criteriaType(dto.getCriteriaType())
                .basePoints(dto.getBasePoints() != null ? dto.getBasePoints() : 0)
                .maxPoints(dto.getMaxPoints())
                .build();
        return toDto(criteriaRepository.save(criteria));
    }

    public RatingCriteriaDto updateCriteria(UUID id, RatingCriteriaDto dto) {
        RatingCriteria criteria = criteriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Criteria not found: " + id));
        if (dto.getWeight() != null) criteria.setWeight(dto.getWeight());
        if (dto.getIsActive() != null) criteria.setIsActive(dto.getIsActive());
        if (dto.getMaxPoints() != null) criteria.setMaxPoints(dto.getMaxPoints());
        if (dto.getName() != null) criteria.setName(dto.getName());
        if (dto.getDescription() != null) criteria.setDescription(dto.getDescription());
        criteria.setUpdatedAt(OffsetDateTime.now());
        return toDto(criteriaRepository.save(criteria));
    }

    private RatingCriteriaDto toDto(RatingCriteria c) {
        return RatingCriteriaDto.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .weight(c.getWeight())
                .isActive(c.getIsActive())
                .criteriaType(c.getCriteriaType())
                .basePoints(c.getBasePoints())
                .maxPoints(c.getMaxPoints())
                .build();
    }
}
