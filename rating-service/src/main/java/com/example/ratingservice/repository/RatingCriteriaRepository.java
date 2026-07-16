package com.example.ratingservice.repository;

import com.example.ratingservice.model.RatingCriteria;
import com.example.ratingservice.model.RatingCriteriaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RatingCriteriaRepository extends JpaRepository<RatingCriteria, UUID> {
    List<RatingCriteria> findByCriteriaType(RatingCriteriaType type);
    List<RatingCriteria> findByIsActiveTrue();
    List<RatingCriteria> findByCriteriaTypeAndIsActiveTrue(RatingCriteriaType type);

    java.util.Optional<RatingCriteria> findByNameAndIsActiveTrue(String name);
}
