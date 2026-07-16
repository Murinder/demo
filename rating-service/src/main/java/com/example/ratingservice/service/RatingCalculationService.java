package com.example.ratingservice.service;

import com.example.ratingservice.RatingConstants;
import com.example.ratingservice.client.ProjectServiceClient;
import com.example.ratingservice.model.*;
import com.example.ratingservice.repository.*;
import com.example.sharedlib.config.RabbitMqAutoConfiguration;
import com.example.sharedlib.dto.ProjectMemberDto;
import com.example.sharedlib.event.EventPublisher;
import com.example.sharedlib.event.RatingEvent;
import com.example.sharedlib.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RatingCalculationService {

    private static final BigDecimal MAX_RAW_SCORE = BigDecimal.valueOf(80);

    private final StudentRatingRepository studentRatingRepository;
    private final LecturerRatingRepository lecturerRatingRepository;
    private final DepartmentRatingRepository departmentRatingRepository;
    private final RatingCriteriaRepository criteriaRepository;
    private final RatingHistoryRepository historyRepository;
    private final StudentRatingService studentRatingService;
    private final EventPublisher eventPublisher;
    private final ProjectServiceClient projectServiceClient;
    private final ObjectMapper objectMapper;

    /**
     * Initialize a zero-score rating record for a newly registered student.
     */
    public void initializeStudentRating(UUID userId) {
        if (!studentRatingRepository.existsById(userId)) {
            try {
                StudentRating rating = StudentRating.builder()
                        .userId(userId)
                        .totalScore(BigDecimal.ZERO)
                        .semester(1)
                        .calculationDetails("{}")
                        .verificationStatus(StudentRating.VerificationStatus.PENDING)
                        .build();
                studentRatingRepository.saveAndFlush(rating);
                log.info("Initialized rating for new student: {}", userId);
            } catch (Exception e) {
                log.info("Rating already exists for student {} (concurrent create)", userId);
            }
        }
    }

    /**
     * Add points to a student's rating for a specific reason.
     */
    public void addStudentPoints(UUID userId, BigDecimal points, String reason, UUID relatedEntityId, Integer semester) {
        log.info("Adding {} points to student {} for: {}", points, userId, reason);

        StudentRating rating = findOrCreateRating(userId, semester);

        rating.setUpdatedAt(OffsetDateTime.now());
        rating = studentRatingRepository.save(rating);

        // Record in history first so rebuild sees it
        recordHistory(userId, null, points, reason, relatedEntityId, semester);

        // Rebuild calculationDetails JSONB and set normalized totalScore
        rebuildCalculationDetails(userId, rating);

        rating = studentRatingRepository.save(rating);

        // Publish recalculated event
        publishRecalculatedEvent(userId, null, rating.getTotalScore());

        // Check and persist any newly earned badges
        studentRatingService.checkAndPersistBadges(userId);
    }

    private StudentRating findOrCreateRating(UUID userId, Integer semester) {
        return studentRatingRepository.findById(userId)
                .orElseGet(() -> {
                    try {
                        StudentRating newRating = StudentRating.builder()
                                .userId(userId)
                                .totalScore(BigDecimal.ZERO)
                                .semester(semester != null ? semester : 1)
                                .calculationDetails("{}")
                                .verificationStatus(StudentRating.VerificationStatus.PENDING)
                                .build();
                        return studentRatingRepository.saveAndFlush(newRating);
                    } catch (Exception e) {
                        // Concurrent insert — retry lookup
                        log.warn("Concurrent rating creation for user {}, retrying lookup", userId);
                        return studentRatingRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("Failed to create or find rating for " + userId));
                    }
                });
    }

    /**
     * Recalculate student rating by summing all history entries.
     */
    public void recalculateStudentRating(UUID userId) {
        log.info("Recalculating full rating for student: {}", userId);

        StudentRating rating = findOrCreateRating(userId, 1);

        rating.setUpdatedAt(OffsetDateTime.now());
        rebuildCalculationDetails(userId, rating);
        rating = studentRatingRepository.save(rating);

        publishRecalculatedEvent(userId, null, rating.getTotalScore());

        // Check and persist badges after recalculation
        studentRatingService.checkAndPersistBadges(userId);
    }

    /**
     * Handle project completion — fetch all project members via Feign and award points to each.
     */
    public void onProjectCompleted(UUID projectId) {
        try {
            ApiResponse<List<ProjectMemberDto>> resp = projectServiceClient.getProjectMembers(projectId);
            if (resp != null && resp.isSuccess() && resp.getData() != null) {
                for (ProjectMemberDto member : resp.getData()) {
                    awardProjectPoints(member.getUserId(), projectId);
                }
            }
        } catch (Exception e) {
            log.error("Failed to fetch project members for project {}: {}", projectId, e.getMessage());
        }
    }

    private void awardProjectPoints(UUID userId, UUID projectId) {
        List<RatingCriteria> criteria = criteriaRepository.findByCriteriaTypeAndIsActiveTrue(RatingCriteriaType.STUDENT);
        BigDecimal points = criteria.stream()
                .filter(c -> c.getName().toLowerCase().contains("project"))
                .map(c -> BigDecimal.valueOf(c.getBasePoints()).multiply(c.getWeight()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (points.compareTo(BigDecimal.ZERO) == 0) {
            points = BigDecimal.TEN;
        }
        addStudentPoints(userId, points, "project_completed", projectId, null);
    }

    /**
     * Handle task completion.
     */
    public void onTaskCompleted(UUID userId, UUID taskId, UUID projectId) {
        BigDecimal points = getPointsForReason("task_completed", 2);
        addStudentPoints(userId, points, "task_completed", taskId, null);
    }

    /**
     * Handle event participation.
     */
    public void onEventParticipation(UUID userId, UUID eventId) {
        BigDecimal points = getPointsForReason("event_participation", 5);
        addStudentPoints(userId, points, "event_participation", eventId, null);
    }

    /**
     * Handle achievement added to portfolio.
     */
    public void onAchievementAdded(UUID userId, UUID achievementId) {
        BigDecimal points = getPointsForReason("achievement_added", 3);
        addStudentPoints(userId, points, "achievement_added", achievementId, null);
    }

    /**
     * Lookup point value for a reason from RatingCriteria table.
     * Falls back to the provided default if no matching criteria is found.
     */
    private BigDecimal getPointsForReason(String reason, int defaultPoints) {
        return criteriaRepository.findByNameAndIsActiveTrue(reason)
                .map(c -> BigDecimal.valueOf(c.getBasePoints()).multiply(c.getWeight()))
                .orElse(BigDecimal.valueOf(defaultPoints));
    }

    /**
     * Rebuild calculationDetails JSONB from history and update totalScore with normalized value.
     */
    private void rebuildCalculationDetails(UUID userId, StudentRating rating) {
        List<RatingHistory> history = historyRepository.findByUserId(userId);

        Map<String, BigDecimal> categoryTotals = new HashMap<>();
        categoryTotals.put("academic", BigDecimal.ZERO);
        categoryTotals.put("activity", BigDecimal.ZERO);
        categoryTotals.put("achievements", BigDecimal.ZERO);

        BigDecimal totalRaw = BigDecimal.ZERO;
        for (RatingHistory h : history) {
            String category = RatingConstants.REASON_TO_CATEGORY.getOrDefault(h.getReason(), "academic");
            categoryTotals.merge(category, h.getScore(), BigDecimal::add);
            totalRaw = totalRaw.add(h.getScore());
        }

        BigDecimal totalNormalized = totalRaw
                .divide(MAX_RAW_SCORE, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .min(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

        try {
            Map<String, Object> details = Map.of(
                    "academic",       Map.of("score", categoryTotals.get("academic")),
                    "activity",       Map.of("score", categoryTotals.get("activity")),
                    "achievements",   Map.of("score", categoryTotals.get("achievements")),
                    "totalRaw",       totalRaw,
                    "totalNormalized", totalNormalized
            );
            rating.setCalculationDetails(objectMapper.writeValueAsString(details));
        } catch (Exception e) {
            log.warn("Failed to serialize calculationDetails for user {}: {}", userId, e.getMessage());
            rating.setCalculationDetails("{}");
        }

        rating.setTotalScore(totalNormalized);
    }

    private void recordHistory(UUID userId, UUID departmentId, BigDecimal score, String reason, UUID relatedEntityId, Integer semester) {
        RatingHistory history = RatingHistory.builder()
                .userId(userId)
                .departmentId(departmentId)
                .score(score)
                .reason(reason)
                .relatedEntityId(relatedEntityId)
                .semester(semester != null ? semester : 1)
                .calculatedAt(OffsetDateTime.now())
                .build();
        historyRepository.save(history);
    }

    private void publishRecalculatedEvent(UUID userId, UUID departmentId, BigDecimal newScore) {
        RatingEvent event = RatingEvent.builder()
                .userId(userId)
                .departmentId(departmentId)
                .newScore(newScore)
                .build();
        event.init("rating-service");
        eventPublisher.publish(RabbitMqAutoConfiguration.RATING_EXCHANGE, "rating.recalculated", event);
    }
}
