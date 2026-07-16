package com.example.ratingservice.service;

import com.example.ratingservice.RatingConstants;
import com.example.ratingservice.client.CoreServiceClient;
import com.example.ratingservice.dto.*;
import com.example.ratingservice.model.EarnedAchievement;
import com.example.ratingservice.model.RatingHistory;
import com.example.ratingservice.model.StudentRating;
import com.example.ratingservice.repository.EarnedAchievementRepository;
import com.example.ratingservice.repository.RatingHistoryRepository;
import com.example.ratingservice.repository.StudentRatingRepository;
import com.example.sharedlib.dto.UserProfileDto;
import com.example.sharedlib.response.ApiResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentRatingService {

    private static final BigDecimal ACADEMIC_MAX = BigDecimal.valueOf(35);
    private static final BigDecimal ACTIVITY_MAX = BigDecimal.valueOf(25);
    private static final BigDecimal ACHIEVEMENTS_MAX = BigDecimal.valueOf(20);


    private final StudentRatingRepository studentRatingRepository;
    private final RatingHistoryRepository historyRepository;
    private final EarnedAchievementRepository earnedAchievementRepository;
    private final CoreServiceClient coreServiceClient;
    private final UserNameCacheService userNameCacheService;
    private final ObjectMapper objectMapper;

    public StudentRatingDto getStudentRating(UUID userId) {
        StudentRating rating = studentRatingRepository.findById(userId).orElse(null);
        if (rating == null) {
            return defaultRatingDto(userId);
        }
        StudentRatingDto dto = toDto(rating);
        enrichWithUserName(dto);
        return dto;
    }

    public List<StudentRatingDto> getTopStudents(int limit) {
        List<StudentRatingDto> dtos = studentRatingRepository.findTopStudents(PageRequest.of(0, limit)).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        enrichWithUserNames(dtos);
        return dtos;
    }

    public LeaderboardDto getLeaderboard(UUID userId, int limit) {
        List<StudentRatingDto> topDtos = studentRatingRepository.findTopStudents(PageRequest.of(0, limit)).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        enrichWithUserNames(topDtos);

        BigDecimal userScore = studentRatingRepository.findById(userId)
                .map(StudentRating::getTotalScore)
                .orElse(BigDecimal.ZERO);
        long userRank = studentRatingRepository.findRankByScore(userScore);
        long totalStudents = studentRatingRepository.count();

        return LeaderboardDto.builder()
                .topStudents(topDtos)
                .userRank(userRank)
                .totalStudents(totalStudents)
                .build();
    }

    public List<RatingHistoryDto> getRatingDetails(UUID userId, int page, int size) {
        return historyRepository.findByUserIdOrderByCalculatedAtDesc(userId, PageRequest.of(page, size))
                .stream()
                .map(this::toHistoryDto)
                .collect(Collectors.toList());
    }

    public RatingBreakdownDto getBreakdown(UUID userId) {
        StudentRating rating = studentRatingRepository.findById(userId).orElse(null);
        if (rating == null) {
            return defaultBreakdownDto(userId);
        }

        BigDecimal academicRaw = BigDecimal.ZERO;
        BigDecimal activityRaw = BigDecimal.ZERO;
        BigDecimal achievementsRaw = BigDecimal.ZERO;
        BigDecimal totalRaw = BigDecimal.ZERO;

        try {
            JsonNode root = objectMapper.readTree(rating.getCalculationDetails());
            academicRaw      = getNodeDecimal(root, "academic");
            activityRaw      = getNodeDecimal(root, "activity");
            achievementsRaw  = getNodeDecimal(root, "achievements");
            // Fallback: try legacy "communication" key for old data
            if (achievementsRaw.compareTo(BigDecimal.ZERO) == 0) {
                achievementsRaw = getNodeDecimal(root, "communication");
            }
            totalRaw         = root.has("totalRaw") ? root.get("totalRaw").decimalValue() : BigDecimal.ZERO;
        } catch (Exception e) {
            log.warn("Could not parse calculationDetails for user {}", userId);
        }

        BigDecimal monthGrowth = historyRepository.sumScoreSince(
                userId,
                OffsetDateTime.now().minusDays(30)
        );

        // Derive extra radar scores and sub-parameters from history data
        List<RatingHistory> history = historyRepository.findByUserId(userId);
        BigDecimal leadershipScore = deriveLeadershipScore(history);
        BigDecimal projectsScore = deriveProjectsScore(history);
        BigDecimal innovationScore = deriveInnovationScore(history);
        Map<String, List<RatingBreakdownDto.SubParameterDto>> subParameters = buildSubParameters(history);

        return RatingBreakdownDto.builder()
                .userId(rating.getUserId())
                .totalScore(rating.getTotalScore())
                .verificationStatus(rating.getVerificationStatus() != null
                        ? rating.getVerificationStatus().name() : "PENDING")
                .semester(rating.getSemester())
                .updatedAt(rating.getUpdatedAt())
                .academicScore(normalizeCategory(academicRaw, ACADEMIC_MAX))
                .activityScore(normalizeCategory(activityRaw, ACTIVITY_MAX))
                .achievementsScore(normalizeCategory(achievementsRaw, ACHIEVEMENTS_MAX))
                .leadershipScore(leadershipScore)
                .projectsScore(projectsScore)
                .innovationScore(innovationScore)
                .monthGrowth(monthGrowth)
                .rawScore(totalRaw)
                .subParameters(subParameters)
                .build();
    }

    public RatingComparisonDto getComparison(UUID userId) {
        RatingBreakdownDto userBreakdown = getBreakdown(userId);

        Object[] avgsRaw = studentRatingRepository.findAverageScores();
        // Spring Data may wrap single-row native query results; unwrap if needed
        Object[] avgs = (avgsRaw.length == 1 && avgsRaw[0] instanceof Object[])
                ? (Object[]) avgsRaw[0] : avgsRaw;
        BigDecimal avgTotal         = avgs.length > 0 ? toBigDecimal(avgs[0]) : BigDecimal.ZERO;
        BigDecimal avgAcademic      = avgs.length > 1 ? toBigDecimal(avgs[1]) : BigDecimal.ZERO;
        BigDecimal avgActivity      = avgs.length > 2 ? toBigDecimal(avgs[2]) : BigDecimal.ZERO;
        BigDecimal avgAchievements  = avgs.length > 3 ? toBigDecimal(avgs[3]) : BigDecimal.ZERO;

        String userName = userNameCacheService.fetchUserName(userId);

        // Fetch cohort averages via user profile info
        BigDecimal groupAvg = BigDecimal.ZERO;
        BigDecimal departmentAvg = BigDecimal.ZERO;
        BigDecimal facultyAvg = BigDecimal.ZERO;
        try {
            ApiResponse<UserProfileDto> profileResp = coreServiceClient.getUserProfile(userId);
            if (profileResp != null && profileResp.isSuccess() && profileResp.getData() != null) {
                UserProfileDto profile = profileResp.getData();
                groupAvg = computeCohortAvg(
                        profile.getGroupName() != null
                                ? coreServiceClient.getStudentIdsByGroup(profile.getGroupName())
                                : null);
                departmentAvg = computeCohortAvg(
                        profile.getDepartmentId() != null
                                ? coreServiceClient.getStudentIdsByDepartment(profile.getDepartmentId())
                                : null);
                facultyAvg = computeCohortAvg(
                        profile.getFacultyId() != null
                                ? coreServiceClient.getStudentIdsByFaculty(profile.getFacultyId())
                                : null);
            }
        } catch (Exception e) {
            log.warn("Could not fetch cohort averages for user {}: {}", userId, e.getMessage());
        }

        return RatingComparisonDto.builder()
                .userId(userId)
                .userName(userName)
                .userAcademic(userBreakdown.getAcademicScore())
                .userActivity(userBreakdown.getActivityScore())
                .userAchievements(userBreakdown.getAchievementsScore())
                .userTotal(userBreakdown.getTotalScore())
                .avgAcademic(avgAcademic)
                .avgActivity(avgActivity)
                .avgAchievements(avgAchievements)
                .avgTotal(avgTotal)
                .groupAvg(groupAvg)
                .departmentAvg(departmentAvg)
                .facultyAvg(facultyAvg)
                .build();
    }

    private BigDecimal computeCohortAvg(ApiResponse<List<UUID>> resp) {
        if (resp == null || !resp.isSuccess() || resp.getData() == null || resp.getData().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return studentRatingRepository.findAverageTotalScoreByUserIds(resp.getData())
                .setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal bd) return bd.setScale(2, RoundingMode.HALF_UP);
        if (value instanceof Number n) return BigDecimal.valueOf(n.doubleValue()).setScale(2, RoundingMode.HALF_UP);
        return BigDecimal.ZERO;
    }

    /**
     * Get achievements from the persistent earned_achievements table.
     */
    public List<RatingAchievementDto> getAchievements(UUID userId) {
        return earnedAchievementRepository.findByUserIdOrderByEarnedAtDesc(userId).stream()
                .map(a -> RatingAchievementDto.builder()
                        .id(a.getId())
                        .title(a.getTitle())
                        .description(a.getDescription())
                        .score(a.getScore())
                        .category(a.getCategory())
                        .earnedAt(a.getEarnedAt())
                        .relatedEntityId(a.getRelatedEntityId())
                        .build())
                .toList();
    }

    /**
     * Check badge thresholds and persist any newly earned badges.
     * Called after points are added to a student's rating.
     */
    @Transactional
    public void checkAndPersistBadges(UUID userId) {
        List<RatingHistory> history = historyRepository.findByUserIdOrderByCalculatedAtDesc(userId);

        Map<String, Long> reasonCounts = history.stream()
                .collect(Collectors.groupingBy(
                        h -> h.getReason() != null ? h.getReason() : "unknown",
                        Collectors.counting()));

        BigDecimal totalScore = studentRatingRepository.findById(userId)
                .map(StudentRating::getTotalScore)
                .orElse(BigDecimal.ZERO);

        long activeMonths = history.stream()
                .filter(h -> h.getCalculatedAt() != null)
                .map(h -> h.getCalculatedAt().getYear() * 100 + h.getCalculatedAt().getMonthValue())
                .distinct()
                .count();

        long tasks = reasonCounts.getOrDefault("task_completed", 0L);
        long projects = reasonCounts.getOrDefault("project_completed", 0L);
        long events = reasonCounts.getOrDefault("event_participation", 0L);

        if (tasks >= 1) tryPersistBadge(userId, "first_steps", "Первые шаги",
                "Выполнена первая задача в проекте", BigDecimal.valueOf(2), "academic");
        if (tasks >= 10) tryPersistBadge(userId, "hard_worker", "Трудяга",
                "Выполнено 10 и более задач", BigDecimal.valueOf(20), "academic");
        if (projects >= 1) tryPersistBadge(userId, "first_project", "Первый проект",
                "Успешно завершён первый проект", BigDecimal.valueOf(10), "academic");
        if (events >= 3) tryPersistBadge(userId, "activist", "Активист",
                "Участие в 3 и более мероприятиях", BigDecimal.valueOf(15), "activity");
        if (tasks >= 5 && projects >= 1) tryPersistBadge(userId, "team_player", "Командный игрок",
                "5 задач и 1 завершённый проект", BigDecimal.valueOf(12), "achievements");
        if (activeMonths >= 3) tryPersistBadge(userId, "consistency", "Регулярность",
                "Активность в 3 и более месяцах", BigDecimal.valueOf(10), "activity");
        if (totalScore.compareTo(BigDecimal.valueOf(70)) >= 0) tryPersistBadge(userId, "good_student", "Хорошист",
                "Общий рейтинг 70 и выше", totalScore, "academic");
        if (totalScore.compareTo(BigDecimal.valueOf(90)) >= 0) tryPersistBadge(userId, "honors", "Отличник",
                "Общий рейтинг 90 и выше", totalScore, "academic");
    }

    private void tryPersistBadge(UUID userId, String badgeKey, String title,
                                  String description, BigDecimal score, String category) {
        if (!earnedAchievementRepository.existsByUserIdAndBadgeKey(userId, badgeKey)) {
            earnedAchievementRepository.save(EarnedAchievement.builder()
                    .userId(userId)
                    .badgeKey(badgeKey)
                    .title(title)
                    .description(description)
                    .score(score)
                    .category(category)
                    .earnedAt(OffsetDateTime.now())
                    .build());
            log.info("Badge '{}' earned by user {}", badgeKey, userId);
        }
    }

    // --- derived radar scores ---

    /**
     * Leadership score: derived from completed tasks + projects combined.
     * Having both tasks AND projects indicates leadership ability.
     * Normalized to 0-100: score = min(100, (tasks * 3 + projects * 15) / 1.0)
     */
    private BigDecimal deriveLeadershipScore(List<RatingHistory> history) {
        long tasks = history.stream().filter(h -> "task_completed".equals(h.getReason())).count();
        long projects = history.stream().filter(h -> "project_completed".equals(h.getReason())).count();
        if (tasks == 0 && projects == 0) return BigDecimal.ZERO;
        double raw = Math.min(100, tasks * 3 + projects * 15);
        return BigDecimal.valueOf(raw).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Projects score: directly based on project completions.
     * Normalized to 0-100: each project = 20 points, capped at 100.
     */
    private BigDecimal deriveProjectsScore(List<RatingHistory> history) {
        long projects = history.stream().filter(h -> "project_completed".equals(h.getReason())).count();
        double raw = Math.min(100, projects * 20);
        return BigDecimal.valueOf(raw).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Innovation score: composite of diverse activity types.
     * Having activity across multiple reason types indicates innovation.
     * Each distinct reason type contributes 25 points, capped at 100.
     */
    private BigDecimal deriveInnovationScore(List<RatingHistory> history) {
        long distinctReasons = history.stream()
                .map(RatingHistory::getReason)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        double raw = Math.min(100, distinctReasons * 25);
        return BigDecimal.valueOf(raw).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Build sub-parameter breakdown grouped by category.
     * Each reason within a category becomes a sub-parameter with name, total score, and count.
     */
    private Map<String, List<RatingBreakdownDto.SubParameterDto>> buildSubParameters(List<RatingHistory> history) {
        Map<String, Map<String, BigDecimal>> categoryReasonScores = new HashMap<>();
        Map<String, Map<String, Long>> categoryReasonCounts = new HashMap<>();

        for (RatingHistory h : history) {
            String reason = h.getReason() != null ? h.getReason() : "unknown";
            String category = RatingConstants.REASON_TO_CATEGORY.getOrDefault(reason, "academic");

            categoryReasonScores
                    .computeIfAbsent(category, k -> new HashMap<>())
                    .merge(reason, h.getScore(), BigDecimal::add);
            categoryReasonCounts
                    .computeIfAbsent(category, k -> new HashMap<>())
                    .merge(reason, 1L, Long::sum);
        }

        Map<String, List<RatingBreakdownDto.SubParameterDto>> result = new HashMap<>();
        for (Map.Entry<String, Map<String, BigDecimal>> entry : categoryReasonScores.entrySet()) {
            String category = entry.getKey();
            Map<String, Long> counts = categoryReasonCounts.getOrDefault(category, Map.of());
            List<RatingBreakdownDto.SubParameterDto> params = entry.getValue().entrySet().stream()
                    .map(e -> RatingBreakdownDto.SubParameterDto.builder()
                            .name(RatingConstants.REASON_TITLES.getOrDefault(e.getKey(), e.getKey()))
                            .score(e.getValue())
                            .count(counts.getOrDefault(e.getKey(), 0L))
                            .build())
                    .collect(Collectors.toList());
            result.put(category, params);
        }
        return result;
    }

    // --- helpers ---

    private void enrichWithUserName(StudentRatingDto dto) {
        dto.setUserName(userNameCacheService.fetchUserName(dto.getUserId()));
    }

    private void enrichWithUserNames(List<StudentRatingDto> dtos) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        SecurityContext securityContext = SecurityContextHolder.getContext();
        List<CompletableFuture<Void>> futures = dtos.stream()
                .map(dto -> CompletableFuture.runAsync(() -> {
                    try {
                        RequestContextHolder.setRequestAttributes(requestAttributes);
                        SecurityContextHolder.setContext(securityContext);
                        dto.setUserName(userNameCacheService.fetchUserName(dto.getUserId()));
                    } catch (Exception e) {
                        log.warn("Could not enrich userName for {}: {}", dto.getUserId(), e.getMessage());
                    } finally {
                        SecurityContextHolder.clearContext();
                        RequestContextHolder.resetRequestAttributes();
                    }
                }))
                .toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private StudentRatingDto defaultRatingDto(UUID userId) {
        StudentRatingDto dto = StudentRatingDto.builder()
                .userId(userId)
                .totalScore(BigDecimal.ZERO)
                .calculationDetails("{}")
                .updatedAt(null)
                .semester(1)
                .verificationStatus("PENDING")
                .build();
        enrichWithUserName(dto);
        return dto;
    }

    private RatingBreakdownDto defaultBreakdownDto(UUID userId) {
        return RatingBreakdownDto.builder()
                .userId(userId)
                .totalScore(BigDecimal.ZERO)
                .verificationStatus("PENDING")
                .semester(1)
                .updatedAt(null)
                .academicScore(BigDecimal.ZERO)
                .activityScore(BigDecimal.ZERO)
                .achievementsScore(BigDecimal.ZERO)
                .leadershipScore(BigDecimal.ZERO)
                .projectsScore(BigDecimal.ZERO)
                .innovationScore(BigDecimal.ZERO)
                .monthGrowth(BigDecimal.ZERO)
                .rawScore(BigDecimal.ZERO)
                .build();
    }

    private BigDecimal getNodeDecimal(JsonNode root, String category) {
        // Try nested format first: {"academic": {"score": 85}}
        JsonNode node = root.get(category);
        if (node != null && node.isObject()) {
            JsonNode score = node.get("score");
            return score != null ? score.decimalValue() : BigDecimal.ZERO;
        }
        // Try nested numeric: {"academic": 85}
        if (node != null && node.isNumber()) {
            return node.decimalValue();
        }
        // Try flat format: {"academicScore": 85}
        JsonNode flat = root.get(category + "Score");
        if (flat != null && flat.isNumber()) {
            return flat.decimalValue();
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal normalizeCategory(BigDecimal raw, BigDecimal max) {
        if (max.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return raw.divide(max, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .min(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private StudentRatingDto toDto(StudentRating r) {
        return StudentRatingDto.builder()
                .userId(r.getUserId())
                .totalScore(r.getTotalScore())
                .calculationDetails(r.getCalculationDetails())
                .updatedAt(r.getUpdatedAt())
                .semester(r.getSemester())
                .verificationStatus(r.getVerificationStatus() != null ? r.getVerificationStatus().name() : "PENDING")
                .build();
    }

    private RatingHistoryDto toHistoryDto(RatingHistory h) {
        return RatingHistoryDto.builder()
                .id(h.getId())
                .userId(h.getUserId())
                .departmentId(h.getDepartmentId())
                .score(h.getScore())
                .calculatedAt(h.getCalculatedAt())
                .reason(h.getReason())
                .criteriaId(h.getCriteriaId())
                .relatedEntityId(h.getRelatedEntityId())
                .semester(h.getSemester())
                .build();
    }
}
