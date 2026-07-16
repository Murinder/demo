package com.example.ratingservice.service;

import com.example.ratingservice.dto.DepartmentRatingDto;
import com.example.ratingservice.model.DepartmentRating;
import com.example.ratingservice.repository.DepartmentRatingRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentRatingServiceTest {

    @Mock
    private DepartmentRatingRepository departmentRatingRepository;

    @InjectMocks
    private DepartmentRatingService departmentRatingService;

    @Test
    void getDepartmentRating_ExistingDepartment_ReturnsDto() {
        UUID departmentId = UUID.randomUUID();
        UUID facultyId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        DepartmentRating rating = DepartmentRating.builder()
                .departmentId(departmentId)
                .totalScore(BigDecimal.valueOf(75))
                .semester(2)
                .calculationDetails("{\"students\":120}")
                .facultyId(facultyId)
                .updatedAt(now)
                .build();

        when(departmentRatingRepository.findById(departmentId)).thenReturn(Optional.of(rating));

        DepartmentRatingDto result = departmentRatingService.getDepartmentRating(departmentId);

        assertNotNull(result);
        assertEquals(departmentId, result.getDepartmentId());
        assertEquals(BigDecimal.valueOf(75), result.getTotalScore());
        assertEquals(2, result.getSemester());
        assertEquals(facultyId, result.getFacultyId());
        assertEquals("{\"students\":120}", result.getCalculationDetails());
    }

    @Test
    void getDepartmentRating_NonExistingDepartment_ThrowsResourceNotFoundException() {
        UUID departmentId = UUID.randomUUID();
        when(departmentRatingRepository.findById(departmentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> departmentRatingService.getDepartmentRating(departmentId));
    }

    @Test
    void getDepartmentRanking_ReturnsRankedList() {
        DepartmentRating r1 = DepartmentRating.builder()
                .departmentId(UUID.randomUUID())
                .totalScore(BigDecimal.valueOf(95))
                .semester(1)
                .facultyId(UUID.randomUUID())
                .build();
        DepartmentRating r2 = DepartmentRating.builder()
                .departmentId(UUID.randomUUID())
                .totalScore(BigDecimal.valueOf(70))
                .semester(1)
                .facultyId(UUID.randomUUID())
                .build();
        DepartmentRating r3 = DepartmentRating.builder()
                .departmentId(UUID.randomUUID())
                .totalScore(BigDecimal.valueOf(60))
                .semester(1)
                .facultyId(UUID.randomUUID())
                .build();

        when(departmentRatingRepository.findAllRanked()).thenReturn(List.of(r1, r2, r3));

        List<DepartmentRatingDto> result = departmentRatingService.getDepartmentRanking();

        assertEquals(3, result.size());
        assertEquals(BigDecimal.valueOf(95), result.get(0).getTotalScore());
        assertEquals(BigDecimal.valueOf(70), result.get(1).getTotalScore());
        assertEquals(BigDecimal.valueOf(60), result.get(2).getTotalScore());
    }

    @Test
    void getDepartmentRanking_EmptyList_ReturnsEmpty() {
        when(departmentRatingRepository.findAllRanked()).thenReturn(List.of());

        List<DepartmentRatingDto> result = departmentRatingService.getDepartmentRanking();

        assertTrue(result.isEmpty());
    }
}
