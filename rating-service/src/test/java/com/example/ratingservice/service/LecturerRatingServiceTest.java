package com.example.ratingservice.service;

import com.example.ratingservice.dto.StudentRatingDto;
import com.example.ratingservice.model.LecturerRating;
import com.example.ratingservice.model.StudentRating;
import com.example.ratingservice.repository.LecturerRatingRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LecturerRatingServiceTest {

    @Mock
    private LecturerRatingRepository lecturerRatingRepository;

    @InjectMocks
    private LecturerRatingService lecturerRatingService;

    @Test
    void getLecturerRating_ExistingUser_ReturnsDto() {
        UUID userId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        LecturerRating rating = LecturerRating.builder()
                .userId(userId)
                .totalScore(BigDecimal.valueOf(88))
                .calculationDetails("{\"courses\":5}")
                .semester(2)
                .verificationStatus(StudentRating.VerificationStatus.VERIFIED)
                .updatedAt(now)
                .build();

        when(lecturerRatingRepository.findById(userId)).thenReturn(Optional.of(rating));

        StudentRatingDto result = lecturerRatingService.getLecturerRating(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(BigDecimal.valueOf(88), result.getTotalScore());
        assertEquals("{\"courses\":5}", result.getCalculationDetails());
        assertEquals(2, result.getSemester());
        assertEquals("VERIFIED", result.getVerificationStatus());
        assertEquals(now, result.getUpdatedAt());
    }

    @Test
    void getLecturerRating_NonExistingUser_ThrowsResourceNotFoundException() {
        UUID userId = UUID.randomUUID();
        when(lecturerRatingRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> lecturerRatingService.getLecturerRating(userId));
    }

    @Test
    void getLecturerRating_NullVerificationStatus_ReturnsNullInDto() {
        UUID userId = UUID.randomUUID();
        LecturerRating rating = LecturerRating.builder()
                .userId(userId)
                .totalScore(BigDecimal.valueOf(50))
                .calculationDetails("{}")
                .semester(1)
                .verificationStatus(null)
                .build();

        when(lecturerRatingRepository.findById(userId)).thenReturn(Optional.of(rating));

        StudentRatingDto result = lecturerRatingService.getLecturerRating(userId);

        assertNull(result.getVerificationStatus());
    }
}
