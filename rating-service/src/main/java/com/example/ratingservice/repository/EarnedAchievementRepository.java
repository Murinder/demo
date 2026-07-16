package com.example.ratingservice.repository;

import com.example.ratingservice.model.EarnedAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EarnedAchievementRepository extends JpaRepository<EarnedAchievement, UUID> {
    List<EarnedAchievement> findByUserIdOrderByEarnedAtDesc(UUID userId);
    boolean existsByUserIdAndBadgeKey(UUID userId, String badgeKey);
}
