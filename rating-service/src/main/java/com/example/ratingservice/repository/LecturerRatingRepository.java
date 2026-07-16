package com.example.ratingservice.repository;

import com.example.ratingservice.model.LecturerRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LecturerRatingRepository extends JpaRepository<LecturerRating, UUID> {
}
