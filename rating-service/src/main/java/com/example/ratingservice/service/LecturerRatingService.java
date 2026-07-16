package com.example.ratingservice.service;

import com.example.ratingservice.dto.StudentRatingDto;
import com.example.ratingservice.model.LecturerRating;
import com.example.ratingservice.repository.LecturerRatingRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LecturerRatingService {

    private final LecturerRatingRepository lecturerRatingRepository;

    public StudentRatingDto getLecturerRating(UUID userId) {
        LecturerRating rating = lecturerRatingRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer rating not found: " + userId));
        return StudentRatingDto.builder()
                .userId(rating.getUserId())
                .totalScore(rating.getTotalScore())
                .calculationDetails(rating.getCalculationDetails())
                .updatedAt(rating.getUpdatedAt())
                .semester(rating.getSemester())
                .verificationStatus(rating.getVerificationStatus() != null ? rating.getVerificationStatus().name() : null)
                .build();
    }
}
