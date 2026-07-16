package com.example.coreservice.service;

import com.example.coreservice.client.EventServiceClient;
import com.example.coreservice.client.ProjectServiceClient;
import com.example.coreservice.client.RatingServiceClient;
import com.example.coreservice.dto.TeacherDetailedDto;
import com.example.coreservice.dto.TeacherDetailedDto.*;
import com.example.coreservice.model.entity.LecturerAcademicMetrics;
import com.example.coreservice.model.entity.User;
import com.example.coreservice.repository.LecturerAcademicMetricsRepository;
import com.example.coreservice.repository.UserRepository;
import com.example.sharedlib.enums.UserRole;
import com.example.sharedlib.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherAggregationService {

    private final UserRepository userRepository;
    private final LecturerAcademicMetricsRepository metricsRepository;
    private final ProjectServiceClient projectServiceClient;
    private final RatingServiceClient ratingServiceClient;
    private final EventServiceClient eventServiceClient;

    private static final List<ActivityPoint> DEFAULT_ACTIVITY = List.of(
            new ActivityPoint("Сен", 0, 0),
            new ActivityPoint("Окт", 0, 0),
            new ActivityPoint("Ноя", 0, 0),
            new ActivityPoint("Дек", 0, 0),
            new ActivityPoint("Янв", 0, 0),
            new ActivityPoint("Фев", 0, 0)
    );

    private static final List<SuccessPoint> DEFAULT_SUCCESS = List.of(
            new SuccessPoint("Отлично", 0),
            new SuccessPoint("Хорошо", 0),
            new SuccessPoint("Удовл.", 0)
    );

    @Cacheable(value = "teachersDetailed", key = "'all'")
    public List<TeacherDetailedDto> getAllTeachersDetailed() {
        List<User> lecturers = userRepository.findByRoleAndIsActiveTrue(UserRole.LECTURER);
        if (lecturers.isEmpty()) {
            return List.of();
        }

        List<UUID> teacherIds = lecturers.stream().map(User::getId).collect(Collectors.toList());

        // Batch load academic metrics from local DB
        Map<UUID, LecturerAcademicMetrics> metricsMap = metricsRepository.findByUserIdIn(teacherIds)
                .stream()
                .collect(Collectors.toMap(LecturerAcademicMetrics::getUserId, Function.identity()));

        // Batch load project stats from project-service
        Map<UUID, Map<String, Object>> projectStatsMap = safeGetBatchProjectStats(teacherIds);

        // Build result for each teacher
        return lecturers.stream().map(user -> {
            UUID userId = user.getId();

            // Academic metrics (local)
            LecturerAcademicMetrics metrics = metricsMap.get(userId);
            int publications = metrics != null ? metrics.getPublications() : 0;
            int grants = metrics != null ? metrics.getGrants() : 0;
            int hours = metrics != null ? metrics.getHours() : 0;
            int consultations = metrics != null ? metrics.getConsultations() : 0;

            // Project stats (from project-service)
            Map<String, Object> pStats = projectStatsMap.getOrDefault(userId, Collections.emptyMap());
            int activeProjects = toInt(pStats.get("activeCount"));
            int completedProjects = toInt(pStats.get("completedCount"));
            int totalProjects = toInt(pStats.get("totalCount"));
            int totalStudents = toInt(pStats.get("totalStudents"));
            int activeStudents = toInt(pStats.get("activeStudents"));
            int completion = totalProjects > 0 ? (completedProjects * 100 / totalProjects) : 0;

            // Rating (from rating-service)
            double rating = safeGetLecturerRating(userId);

            // Defense grades (from event-service)
            List<Integer> grades = safeGetDefenseGrades(userId);
            double avgGrade = grades.isEmpty() ? 0.0 :
                    grades.stream().mapToInt(Integer::intValue).average().orElse(0.0);

            // Grade distribution for success chart
            List<SuccessPoint> success = buildGradeDistribution(grades);

            String name = buildName(user);

            return TeacherDetailedDto.builder()
                    .id(userId.toString())
                    .name(name)
                    .title("Преподаватель")
                    .rating(rating)
                    .activeProjects(activeProjects)
                    .totalProjects(totalProjects)
                    .students(totalStudents)
                    .completion(completion)
                    .avgGrade(Math.round(avgGrade * 10.0) / 10.0)
                    .publications(publications)
                    .grants(grants)
                    .hours(hours)
                    .consultations(consultations)
                    .activity(DEFAULT_ACTIVITY)
                    .success(success)
                    .projectsStats(new ProjectsStats(activeProjects, completedProjects))
                    .studentsStats(new StudentsStats(totalStudents, activeStudents))
                    .scienceStats(new ScienceStats(publications, grants))
                    .build();
        }).collect(Collectors.toList());
    }

    private Map<UUID, Map<String, Object>> safeGetBatchProjectStats(List<UUID> teacherIds) {
        try {
            ApiResponse<List<Map<String, Object>>> response = projectServiceClient.getBatchTeacherStats(teacherIds);
            if (response != null && response.getData() != null) {
                return response.getData().stream()
                        .filter(m -> m.get("userId") != null)
                        .collect(Collectors.toMap(
                                m -> UUID.fromString(m.get("userId").toString()),
                                Function.identity()
                        ));
            }
        } catch (Exception e) {
            log.warn("Failed to get batch project stats: {}", e.getMessage());
        }
        return Collections.emptyMap();
    }

    private double safeGetLecturerRating(UUID userId) {
        try {
            Map<String, Object> rating = ratingServiceClient.getLecturerRating(userId);
            if (rating != null && rating.get("totalScore") != null) {
                Object score = rating.get("totalScore");
                if (score instanceof Number) {
                    return ((Number) score).doubleValue();
                }
                return new BigDecimal(score.toString()).doubleValue();
            }
        } catch (Exception e) {
            log.debug("No rating found for lecturer {}: {}", userId, e.getMessage());
        }
        return 0.0;
    }

    @SuppressWarnings("unchecked")
    private List<Integer> safeGetDefenseGrades(UUID userId) {
        try {
            ApiResponse<List<Map<String, Object>>> response = eventServiceClient.getDefensesBySupervisor(userId);
            if (response != null && response.getData() != null) {
                return response.getData().stream()
                        .filter(d -> d.get("grade") != null && "COMPLETED".equals(d.get("status")))
                        .map(d -> toInt(d.get("grade")))
                        .filter(g -> g > 0)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.debug("No defenses found for supervisor {}: {}", userId, e.getMessage());
        }
        return List.of();
    }

    private List<SuccessPoint> buildGradeDistribution(List<Integer> grades) {
        if (grades.isEmpty()) {
            return DEFAULT_SUCCESS;
        }
        int excellent = 0, good = 0, satisfactory = 0;
        for (int g : grades) {
            if (g >= 5) excellent++;
            else if (g == 4) good++;
            else if (g == 3) satisfactory++;
        }
        return List.of(
                new SuccessPoint("Отлично", excellent),
                new SuccessPoint("Хорошо", good),
                new SuccessPoint("Удовл.", satisfactory)
        );
    }

    private String buildName(User user) {
        String name = joinParts(user.getLastName(), user.getFirstName());
        if (name.isBlank()) {
            return user.getEmail() != null ? user.getEmail() : "Преподаватель";
        }
        return name;
    }

    private String joinParts(String... parts) {
        return Arrays.stream(parts)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" "));
    }

    private int toInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
