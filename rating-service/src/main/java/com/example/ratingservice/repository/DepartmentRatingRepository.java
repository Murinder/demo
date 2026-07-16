package com.example.ratingservice.repository;

import com.example.ratingservice.model.DepartmentRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DepartmentRatingRepository extends JpaRepository<DepartmentRating, UUID> {
    List<DepartmentRating> findByFacultyId(UUID facultyId);

    @Query("SELECT dr FROM DepartmentRating dr ORDER BY dr.totalScore DESC")
    List<DepartmentRating> findAllRanked();
}
