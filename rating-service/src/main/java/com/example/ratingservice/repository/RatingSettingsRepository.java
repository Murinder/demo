package com.example.ratingservice.repository;

import com.example.ratingservice.model.RatingSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingSettingsRepository extends JpaRepository<RatingSettings, UUID> {
    Optional<RatingSettings> findByIsActiveTrueAndSemester(Integer semester);
    Optional<RatingSettings> findByIsActiveTrue();
}
