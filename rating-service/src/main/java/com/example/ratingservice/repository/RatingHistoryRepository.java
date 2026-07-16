package com.example.ratingservice.repository;

import com.example.ratingservice.model.RatingHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface RatingHistoryRepository extends JpaRepository<RatingHistory, UUID> {
    List<RatingHistory> findByUserId(UUID userId);
    List<RatingHistory> findByDepartmentId(UUID departmentId);
    List<RatingHistory> findByUserIdAndSemester(UUID userId, Integer semester);
    List<RatingHistory> findByUserIdOrderByCalculatedAtDesc(UUID userId);
    Page<RatingHistory> findByUserIdOrderByCalculatedAtDesc(UUID userId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(h.score), 0) FROM RatingHistory h " +
           "WHERE h.userId = :userId AND h.calculatedAt >= :since")
    BigDecimal sumScoreSince(@Param("userId") UUID userId,
                             @Param("since") OffsetDateTime since);
}
