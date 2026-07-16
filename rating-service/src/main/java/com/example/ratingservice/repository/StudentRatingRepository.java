package com.example.ratingservice.repository;

import com.example.ratingservice.model.StudentRating;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentRatingRepository extends JpaRepository<StudentRating, UUID> {
    @Query("SELECT sr FROM StudentRating sr ORDER BY sr.totalScore DESC")
    List<StudentRating> findTopStudents(Pageable pageable);

    List<StudentRating> findBySemester(Integer semester);

    /**
     * Returns platform-wide average scores in a single DB round-trip.
     * Result columns: [avgTotal, avgAcademic, avgActivity, avgCommunication]
     * Category scores are normalized to 0-100 using the standard weights (40/30/30).
     */
    @Query(value =
            "SELECT COALESCE(AVG(total_score), 0), " +
            "COALESCE(AVG(LEAST(COALESCE(" +
            "  CAST(calculation_details->'academic'->>'score' AS numeric), " +
            "  CAST(calculation_details->>'academicScore' AS numeric), " +
            "  CAST(calculation_details->>'academic' AS numeric), 0" +
            ") / 35.0 * 100, 100)), 0), " +
            "COALESCE(AVG(LEAST(COALESCE(" +
            "  CAST(calculation_details->'activity'->>'score' AS numeric), " +
            "  CAST(calculation_details->>'activityScore' AS numeric), " +
            "  CAST(calculation_details->>'activity' AS numeric), 0" +
            ") / 25.0 * 100, 100)), 0), " +
            "COALESCE(AVG(LEAST(COALESCE(" +
            "  CAST(calculation_details->'achievements'->>'score' AS numeric), " +
            "  CAST(calculation_details->'communication'->>'score' AS numeric), " +
            "  CAST(calculation_details->>'achievementsScore' AS numeric), " +
            "  CAST(calculation_details->>'communicationScore' AS numeric), " +
            "  CAST(calculation_details->>'achievements' AS numeric), " +
            "  CAST(calculation_details->>'communication' AS numeric), 0" +
            ") / 20.0 * 100, 100)), 0) " +
            "FROM student_ratings",
            nativeQuery = true)
    Object[] findAverageScores();

    /**
     * Returns the 1-based rank of a student (number of students with a strictly higher score + 1).
     */
    @Query("SELECT COUNT(sr) + 1 FROM StudentRating sr WHERE sr.totalScore > :score")
    long findRankByScore(@Param("score") java.math.BigDecimal score);

    /**
     * Returns average totalScore for a set of user IDs.
     */
    @Query("SELECT COALESCE(AVG(sr.totalScore), 0) FROM StudentRating sr WHERE sr.userId IN :userIds")
    java.math.BigDecimal findAverageTotalScoreByUserIds(@Param("userIds") java.util.Collection<java.util.UUID> userIds);
}
