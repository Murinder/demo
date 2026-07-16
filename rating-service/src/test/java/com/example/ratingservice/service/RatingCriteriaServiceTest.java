package com.example.ratingservice.service;

import com.example.ratingservice.dto.RatingCriteriaDto;
import com.example.ratingservice.model.RatingCriteria;
import com.example.ratingservice.model.RatingCriteriaType;
import com.example.ratingservice.repository.RatingCriteriaRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingCriteriaServiceTest {

    @Mock
    private RatingCriteriaRepository criteriaRepository;

    @InjectMocks
    private RatingCriteriaService ratingCriteriaService;

    @Test
    void getAllCriteria_ReturnsList() {
        RatingCriteria c1 = RatingCriteria.builder()
                .id(UUID.randomUUID())
                .name("Project completion")
                .weight(BigDecimal.valueOf(1.5))
                .isActive(true)
                .criteriaType(RatingCriteriaType.STUDENT)
                .basePoints(10)
                .maxPoints(100)
                .build();
        RatingCriteria c2 = RatingCriteria.builder()
                .id(UUID.randomUUID())
                .name("Course quality")
                .weight(BigDecimal.valueOf(2.0))
                .isActive(true)
                .criteriaType(RatingCriteriaType.LECTURER)
                .basePoints(5)
                .maxPoints(50)
                .build();

        when(criteriaRepository.findAll()).thenReturn(List.of(c1, c2));

        List<RatingCriteriaDto> result = ratingCriteriaService.getAllCriteria();

        assertEquals(2, result.size());
        assertEquals("Project completion", result.get(0).getName());
        assertEquals("Course quality", result.get(1).getName());
    }

    @Test
    void getActiveCriteriaByType_FiltersCorrectly() {
        RatingCriteria criteria = RatingCriteria.builder()
                .id(UUID.randomUUID())
                .name("Student activity")
                .weight(BigDecimal.ONE)
                .isActive(true)
                .criteriaType(RatingCriteriaType.STUDENT)
                .basePoints(5)
                .build();

        when(criteriaRepository.findByCriteriaTypeAndIsActiveTrue(RatingCriteriaType.STUDENT))
                .thenReturn(List.of(criteria));

        List<RatingCriteriaDto> result = ratingCriteriaService.getActiveCriteriaByType(RatingCriteriaType.STUDENT);

        assertEquals(1, result.size());
        assertEquals(RatingCriteriaType.STUDENT, result.get(0).getCriteriaType());
        assertTrue(result.get(0).getIsActive());
    }

    @Test
    void createCriteria_SavesAndReturnsDto() {
        RatingCriteriaDto dto = RatingCriteriaDto.builder()
                .name("New criteria")
                .description("Test description")
                .weight(BigDecimal.valueOf(1.5))
                .isActive(true)
                .criteriaType(RatingCriteriaType.DEPARTMENT)
                .basePoints(10)
                .maxPoints(50)
                .build();

        RatingCriteria saved = RatingCriteria.builder()
                .id(UUID.randomUUID())
                .name("New criteria")
                .description("Test description")
                .weight(BigDecimal.valueOf(1.5))
                .isActive(true)
                .criteriaType(RatingCriteriaType.DEPARTMENT)
                .basePoints(10)
                .maxPoints(50)
                .build();

        when(criteriaRepository.save(any(RatingCriteria.class))).thenReturn(saved);

        RatingCriteriaDto result = ratingCriteriaService.createCriteria(dto);

        assertNotNull(result.getId());
        assertEquals("New criteria", result.getName());
        assertEquals(RatingCriteriaType.DEPARTMENT, result.getCriteriaType());
        assertEquals(10, result.getBasePoints());
        assertEquals(50, result.getMaxPoints());
        verify(criteriaRepository).save(any(RatingCriteria.class));
    }

    @Test
    void createCriteria_NullIsActive_DefaultsToTrue() {
        RatingCriteriaDto dto = RatingCriteriaDto.builder()
                .name("Default active")
                .weight(BigDecimal.ONE)
                .isActive(null)
                .criteriaType(RatingCriteriaType.STUDENT)
                .basePoints(null)
                .build();

        when(criteriaRepository.save(any(RatingCriteria.class))).thenAnswer(inv -> {
            RatingCriteria arg = inv.getArgument(0);
            arg.setId(UUID.randomUUID());
            return arg;
        });

        RatingCriteriaDto result = ratingCriteriaService.createCriteria(dto);

        assertTrue(result.getIsActive());
        assertEquals(0, result.getBasePoints());
    }

    @Test
    void updateCriteria_ExistingCriteria_UpdatesFields() {
        UUID id = UUID.randomUUID();
        RatingCriteria existing = RatingCriteria.builder()
                .id(id)
                .name("Old name")
                .description("Old desc")
                .weight(BigDecimal.ONE)
                .isActive(true)
                .criteriaType(RatingCriteriaType.STUDENT)
                .basePoints(5)
                .maxPoints(50)
                .build();

        RatingCriteriaDto updateDto = RatingCriteriaDto.builder()
                .name("Updated name")
                .weight(BigDecimal.valueOf(2.0))
                .maxPoints(100)
                .build();

        when(criteriaRepository.findById(id)).thenReturn(Optional.of(existing));
        when(criteriaRepository.save(any(RatingCriteria.class))).thenAnswer(inv -> inv.getArgument(0));

        RatingCriteriaDto result = ratingCriteriaService.updateCriteria(id, updateDto);

        assertEquals("Updated name", result.getName());
        assertEquals(BigDecimal.valueOf(2.0), result.getWeight());
        assertEquals(100, result.getMaxPoints());
    }

    @Test
    void updateCriteria_NonExistingCriteria_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(criteriaRepository.findById(id)).thenReturn(Optional.empty());

        RatingCriteriaDto dto = RatingCriteriaDto.builder().name("X").build();

        assertThrows(ResourceNotFoundException.class, () -> ratingCriteriaService.updateCriteria(id, dto));
    }
}
