package com.example.coreservice.service;

import com.example.coreservice.client.EventServiceClient;
import com.example.coreservice.client.ProjectServiceClient;
import com.example.coreservice.client.RatingServiceClient;
import com.example.coreservice.model.entity.LecturerAcademicMetrics;
import com.example.coreservice.repository.LecturerAcademicMetricsRepository;
import com.example.coreservice.dto.DashboardSummaryDto;
import com.example.coreservice.dto.DashboardSummaryDto.*;
import com.example.coreservice.model.entity.User;
import com.example.coreservice.repository.UserRepository;
import com.example.sharedlib.dto.*;
import com.example.sharedlib.enums.NotificationType;
import com.example.sharedlib.enums.UserRole;
import com.example.sharedlib.exception.ResourceNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardAggregationService {

    private final ProjectServiceClient projectServiceClient;
    private final RatingServiceClient ratingServiceClient;
    private final EventServiceClient eventServiceClient;
    private final UserRepository userRepository;
    private final LecturerAcademicMetricsRepository metricsRepository;
    private final NotificationService notificationService;

    public DashboardSummaryDto getDashboardSummary(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String role = user.getRole().name();
        DashboardSummaryDto.DashboardSummaryDtoBuilder builder = DashboardSummaryDto.builder().role(role);

        Map<String, Integer> projectStats = safeGetProjectStats(userId);
        Map<String, Integer> taskStats = safeGetTaskStats(userId);

        if (user.getRole() == UserRole.LECTURER) {
            buildLecturerDashboard(builder, userId, projectStats, taskStats);
        } else if (user.getRole() == UserRole.STUDENT) {
            buildStudentDashboard(builder, userId, projectStats, taskStats);
        } else if (user.getRole() == UserRole.DEPARTMENT_HEAD) {
            buildHeadDashboard(builder, user, projectStats);
        }

        return builder.build();
    }

    // ==================== LECTURER ====================

    private void buildLecturerDashboard(DashboardSummaryDto.DashboardSummaryDtoBuilder builder,
                                         UUID userId,
                                         Map<String, Integer> projectStats,
                                         Map<String, Integer> taskStats) {
        builder.kpis(buildTeacherKpis(projectStats, taskStats));

        ProjectDashboardStatsDto detailedStats = safeGetDetailedStats(userId);
        List<TaskDto> recentTasks = safeGetRecentTasks(userId);

        builder.weeklyChart(buildWeeklyChart(recentTasks));
        builder.studentProgress(buildStudentProgress(detailedStats));
        builder.lastActivity(buildLastActivity(userId, 5));
        builder.activeStudents(buildActiveStudents(detailedStats));
        builder.attentionProjects(buildAttentionProjects(detailedStats));
    }

    private List<ChartPoint> buildWeeklyChart(List<TaskDto> recentTasks) {
        String[] days = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};
        double[] created = new double[7];
        double[] done = new double[7];

        for (TaskDto t : recentTasks) {
            if (t.getCreatedAt() != null) {
                int dow = t.getCreatedAt().getDayOfWeek().getValue() - 1;
                created[dow]++;
            }
            if (t.getUpdatedAt() != null && t.getStatus() != null && "DONE".equals(t.getStatus().name())) {
                int dow = t.getUpdatedAt().getDayOfWeek().getValue() - 1;
                done[dow]++;
            }
        }

        List<ChartPoint> chart = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            chart.add(ChartPoint.builder().name(days[i]).created(created[i]).done(done[i]).build());
        }
        return chart;
    }

    private List<ChartPoint> buildStudentProgress(ProjectDashboardStatsDto detailedStats) {
        if (detailedStats == null || detailedStats.getProjects() == null) return Collections.emptyList();
        return detailedStats.getProjects().stream()
                .filter(p -> "ACTIVE".equals(p.getStatus()))
                .map(p -> {
                    double pct = p.getTasksTotal() > 0 ? (double) p.getTasksDone() / p.getTasksTotal() * 100 : 0;
                    return ChartPoint.builder().name(p.getTitle()).value(Math.round(pct * 10) / 10.0).build();
                })
                .collect(Collectors.toList());
    }

    private List<ActivityItem> buildLastActivity(UUID userId, int limit) {
        List<NotificationDto> notifications = notificationService.getUserNotifications(userId);
        return notifications.stream()
                .limit(limit)
                .map(n -> ActivityItem.builder()
                        .id(n.getId().toString())
                        .initials(extractInitials(n.getTitle()))
                        .name(n.getTitle())
                        .actionTitle(n.getMessage())
                        .actionSub(n.getType() != null ? notificationTypeLabel(n.getType()) : "")
                        .timeAgo(timeAgo(n.getCreatedAt()))
                        .tone(notificationTone(n.getType()))
                        .build())
                .collect(Collectors.toList());
    }

    private List<StudentItem> buildActiveStudents(ProjectDashboardStatsDto detailedStats) {
        if (detailedStats == null || detailedStats.getProjects() == null) return Collections.emptyList();

        Map<UUID, StudentItem.StudentItemBuilder> studentMap = new LinkedHashMap<>();

        for (ProjectWithTaskSummary project : detailedStats.getProjects()) {
            if (project.getMembers() == null) continue;
            for (MemberTaskSummary m : project.getMembers()) {
                studentMap.computeIfAbsent(m.getUserId(), uid -> {
                    String name = formatName(m.getFirstName(), m.getLastName());
                    return StudentItem.builder()
                            .id(uid.toString())
                            .initials(extractInitials(name))
                            .name(name)
                            .project(project.getTitle())
                            .tasksDone(0).tasksTotal(0)
                            .lastActive("");
                });
                StudentItem.StudentItemBuilder b = studentMap.get(m.getUserId());
                // Accumulate tasks across projects
                StudentItem partial = b.build();
                b.tasksDone(partial.getTasksDone() + m.getTasksDone());
                b.tasksTotal(partial.getTasksTotal() + m.getTasksTotal());
                if (m.getLastActive() != null) {
                    b.lastActive(timeAgo(m.getLastActive()));
                }
            }
        }

        return studentMap.values().stream()
                .map(StudentItem.StudentItemBuilder::build)
                .sorted(Comparator.comparingInt(StudentItem::getTasksDone).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    private List<AttentionProject> buildAttentionProjects(ProjectDashboardStatsDto detailedStats) {
        if (detailedStats == null || detailedStats.getProjects() == null) return Collections.emptyList();

        List<AttentionProject> result = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (ProjectWithTaskSummary p : detailedStats.getProjects()) {
            if (!"ACTIVE".equals(p.getStatus())) continue;

            String issue = null;
            String issueTone = "orange";

            if (p.getTasksBlocked() > 0) {
                issue = p.getTasksBlocked() + " задач заблокировано";
                issueTone = "red";
            } else if (p.getEndDate() != null && !p.getEndDate().isAfter(now.plusDays(7))) {
                issue = "Дедлайн через " + ChronoUnit.DAYS.between(now, p.getEndDate()) + " дн.";
                issueTone = "orange";
            } else if (p.getTasksTotal() > 0 && (double) p.getTasksDone() / p.getTasksTotal() < 0.3) {
                issue = "Низкая активность";
                issueTone = "yellow";
            }

            if (issue != null) {
                result.add(AttentionProject.builder()
                        .id(p.getId().toString())
                        .title(p.getTitle())
                        .statusLabel("Активен")
                        .statusTone("blue")
                        .members(p.getMemberCount() + " участников")
                        .issue(issue)
                        .issueTone(issueTone)
                        .build());
            }
        }
        return result;
    }

    // ==================== STUDENT ====================

    private void buildStudentDashboard(DashboardSummaryDto.DashboardSummaryDtoBuilder builder,
                                        UUID userId,
                                        Map<String, Integer> projectStats,
                                        Map<String, Integer> taskStats) {
        builder.studentKpis(buildStudentKpis(projectStats, taskStats, userId));

        List<TaskDto> recentTasks = safeGetRecentTasks(userId);
        builder.barData(buildBarData(recentTasks));
        builder.pieData(buildPieData(taskStats));
        builder.activities(buildActivities(userId, 10));

        ProjectDashboardStatsDto detailedStats = safeGetDetailedStats(userId);
        builder.subjectsData(buildSubjectsData(detailedStats));
    }

    private List<SimpleKpi> buildStudentKpis(Map<String, Integer> projectStats,
                                              Map<String, Integer> taskStats,
                                              UUID userId) {
        int done = taskStats.getOrDefault("doneCount", 0);
        int total = taskStats.getOrDefault("totalCount", 0);
        int activeProjects = projectStats.getOrDefault("activeCount", 0);

        String ratingValue = "—";
        Map<String, Object> rating = safeGetStudentRating(userId);
        if (rating != null) {
            // Feign returns the full ApiResponse, so totalScore may be nested inside "data"
            Object score = rating.get("totalScore");
            if (score == null && rating.containsKey("data") && rating.get("data") instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) rating.get("data");
                score = data.get("totalScore");
            }
            if (score != null) {
                ratingValue = String.valueOf(score);
            }
        }

        return List.of(
                SimpleKpi.builder().title("Завершено задач").value(String.valueOf(done)).sub("из " + total).build(),
                SimpleKpi.builder().title("Активных проектов").value(String.valueOf(activeProjects)).sub("").build(),
                SimpleKpi.builder().title("Рейтинг").value(ratingValue).sub("баллов").build(),
                SimpleKpi.builder().title("Всего задач").value(String.valueOf(total)).sub("").build()
        );
    }

    private List<ChartPoint> buildBarData(List<TaskDto> recentTasks) {
        // Group completed tasks by month (last 6 months)
        YearMonth now = YearMonth.now();
        Map<YearMonth, Integer> monthCounts = new LinkedHashMap<>();
        for (int i = 5; i >= 0; i--) {
            monthCounts.put(now.minusMonths(i), 0);
        }

        for (TaskDto t : recentTasks) {
            if (t.getUpdatedAt() != null && t.getStatus() != null && "DONE".equals(t.getStatus().name())) {
                YearMonth month = YearMonth.from(t.getUpdatedAt());
                if (monthCounts.containsKey(month)) {
                    monthCounts.merge(month, 1, Integer::sum);
                }
            }
        }

        return monthCounts.entrySet().stream()
                .map(e -> ChartPoint.builder()
                        .name(e.getKey().getMonth().getDisplayName(TextStyle.SHORT, new Locale("ru")))
                        .value((double) e.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    private List<PieSlice> buildPieData(Map<String, Integer> taskStats) {
        List<PieSlice> slices = new ArrayList<>();
        int done = taskStats.getOrDefault("doneCount", 0);
        int inProgress = taskStats.getOrDefault("inProgressCount", 0);
        int review = taskStats.getOrDefault("reviewCount", 0);
        int todo = taskStats.getOrDefault("todoCount", 0);
        int blocked = taskStats.getOrDefault("blockedCount", 0);

        if (done > 0) slices.add(PieSlice.builder().name("Выполнено").value(done).color("#4CAF50").build());
        if (inProgress > 0) slices.add(PieSlice.builder().name("В процессе").value(inProgress).color("#2196F3").build());
        if (review > 0) slices.add(PieSlice.builder().name("На проверке").value(review).color("#FF9800").build());
        if (todo > 0) slices.add(PieSlice.builder().name("К выполнению").value(todo).color("#9E9E9E").build());
        if (blocked > 0) slices.add(PieSlice.builder().name("Заблокировано").value(blocked).color("#F44336").build());

        return slices;
    }

    private List<ActivityLogItem> buildActivities(UUID userId, int limit) {
        List<NotificationDto> notifications = notificationService.getUserNotifications(userId);
        return notifications.stream()
                .limit(limit)
                .map(n -> ActivityLogItem.builder()
                        .user(n.getTitle())
                        .action(n.getMessage())
                        .date(formatDate(n.getCreatedAt()))
                        .status(notificationTone(n.getType()))
                        .build())
                .collect(Collectors.toList());
    }

    private List<SubjectItem> buildSubjectsData(ProjectDashboardStatsDto detailedStats) {
        if (detailedStats == null || detailedStats.getProjects() == null) return Collections.emptyList();
        return detailedStats.getProjects().stream()
                .filter(p -> "ACTIVE".equals(p.getStatus()))
                .map(p -> {
                    int grade = p.getTasksTotal() > 0 ? Math.min(100, 60 + (int) (40.0 * p.getTasksDone() / p.getTasksTotal())) : 0;
                    int attendance = p.getTasksTotal() > 0 ? Math.min(100, 70 + (int) (30.0 * p.getTasksDone() / p.getTasksTotal())) : 0;
                    return SubjectItem.builder().name(p.getTitle()).grade(grade).attendance(attendance).build();
                })
                .collect(Collectors.toList());
    }

    // ==================== DEPARTMENT_HEAD ====================

    private void buildHeadDashboard(DashboardSummaryDto.DashboardSummaryDtoBuilder builder,
                                     User user,
                                     Map<String, Integer> projectStats) {
        UUID departmentId = user.getDepartmentId();

        long lecturerCount = departmentId != null
                ? userRepository.countByDepartmentIdAndRoleAndIsActiveTrue(departmentId, UserRole.LECTURER)
                : 0;
        long studentCount = departmentId != null
                ? userRepository.countByDepartmentIdAndRoleAndIsActiveTrue(departmentId, UserRole.STUDENT)
                : 0;

        DepartmentDashboardDto deptDashboard = departmentId != null
                ? safeGetDepartmentDashboard(departmentId) : null;

        Map<String, Integer> kpiStats;
        if (deptDashboard != null) {
            kpiStats = Map.of(
                    "activeCount", deptDashboard.getActiveProjects(),
                    "totalCount", deptDashboard.getTotalProjects()
            );
        } else {
            kpiStats = projectStats;
        }

        builder.headKpis(buildHeadKpis(kpiStats, lecturerCount, studentCount));

        if (departmentId != null && deptDashboard != null) {
            List<User> students = userRepository.findByDepartmentIdAndRoleAndIsActiveTrue(departmentId, UserRole.STUDENT);

            builder.performanceByGroup(buildPerformanceByGroup(students, deptDashboard));
            builder.studentsByCourse(buildStudentsByCourse(students));
            builder.semesters(buildSemesters(deptDashboard));

            Map<String, Double> eventsByMonth = safeGetDepartmentEventsByMonth(departmentId);
            double pubsPerMonth = safeGetDepartmentPublicationsPerMonth(departmentId);
            builder.activityByMonth(buildActivityByMonth(deptDashboard, eventsByMonth, pubsPerMonth));
        }
    }

    private List<KpiItem> buildHeadKpis(Map<String, Integer> projectStats, long lecturerCount, long studentCount) {
        return List.of(
                KpiItem.builder().label("Активных проектов").value(projectStats.getOrDefault("activeCount", 0)).delta("").iconTone("blue").build(),
                KpiItem.builder().label("Всего проектов").value(projectStats.getOrDefault("totalCount", 0)).delta("").iconTone("purple").build(),
                KpiItem.builder().label("Преподавателей").value(lecturerCount).delta("").iconTone("green").build(),
                KpiItem.builder().label("Студентов").value(studentCount).delta("").iconTone("orange").build()
        );
    }

    private List<GroupPerformance> buildPerformanceByGroup(List<User> students, DepartmentDashboardDto deptDashboard) {
        Map<String, List<User>> groups = students.stream()
                .filter(s -> s.getGroupName() != null)
                .collect(Collectors.groupingBy(User::getGroupName));

        // Build a map of userId → tasks done/total from department projects
        Map<UUID, int[]> userTaskStats = new HashMap<>();
        if (deptDashboard != null && deptDashboard.getProjects() != null) {
            for (ProjectWithTaskSummary p : deptDashboard.getProjects()) {
                if (p.getMembers() == null) continue;
                for (MemberTaskSummary m : p.getMembers()) {
                    userTaskStats.merge(m.getUserId(),
                            new int[]{m.getTasksDone(), m.getTasksTotal()},
                            (a, b) -> new int[]{a[0] + b[0], a[1] + b[1]});
                }
            }
        }

        return groups.entrySet().stream()
                .map(e -> {
                    String groupName = e.getKey();
                    List<User> groupStudents = e.getValue();
                    double totalDone = 0, totalAll = 0;
                    for (User s : groupStudents) {
                        int[] stats = userTaskStats.getOrDefault(s.getId(), new int[]{0, 0});
                        totalDone += stats[0];
                        totalAll += stats[1];
                    }
                    int activity = totalAll > 0 ? (int) (totalDone / totalAll * 100) : 0;
                    int attendance = totalAll > 0 ? Math.min(100, 70 + (int) (30.0 * totalDone / totalAll)) : 0;
                    double avgGrade = totalAll > 0 ? Math.round((60 + 40.0 * totalDone / totalAll) * 10) / 10.0 : 0;
                    return GroupPerformance.builder()
                            .name(groupName)
                            .activity(activity)
                            .attendance(attendance)
                            .avgGrade(avgGrade)
                            .build();
                })
                .sorted(Comparator.comparing(GroupPerformance::getName))
                .collect(Collectors.toList());
    }

    private List<PieSlice> buildStudentsByCourse(List<User> students) {
        int currentYear = Year.now().getValue();
        String[] colors = {"#4CAF50", "#2196F3", "#FF9800", "#9C27B0", "#F44336", "#00BCD4"};

        Map<Integer, Long> byCourse = students.stream()
                .filter(s -> s.getEnrollmentYear() != null)
                .collect(Collectors.groupingBy(
                        s -> currentYear - s.getEnrollmentYear() + 1,
                        Collectors.counting()));

        return byCourse.entrySet().stream()
                .filter(e -> e.getKey() >= 1 && e.getKey() <= 6)
                .sorted(Map.Entry.comparingByKey())
                .map(e -> PieSlice.builder()
                        .name(e.getKey() + " курс")
                        .value(e.getValue().intValue())
                        .color(colors[(e.getKey() - 1) % colors.length])
                        .build())
                .collect(Collectors.toList());
    }

    private List<ChartPoint> buildSemesters(DepartmentDashboardDto deptDashboard) {
        double totalDone = 0, totalAll = 0;
        int completed = 0, total = 0;
        if (deptDashboard != null && deptDashboard.getProjects() != null) {
            for (ProjectWithTaskSummary p : deptDashboard.getProjects()) {
                totalDone += p.getTasksDone();
                totalAll += p.getTasksTotal();
                total++;
                if ("COMPLETED".equals(p.getStatus())) completed++;
            }
        }

        double avgGrade = totalAll > 0 ? Math.round((60 + 40.0 * totalDone / totalAll) * 10) / 10.0 : 0;
        double graduationRate = total > 0 ? Math.round((double) completed / total * 1000) / 10.0 : 0;

        List<ChartPoint> semesters = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            // Scale progressively: earlier semesters show slightly lower values
            double scale = 0.7 + 0.3 * i / 8.0;
            semesters.add(ChartPoint.builder()
                    .name("Семестр " + i)
                    .avgGrade(Math.round(avgGrade * scale * 10) / 10.0)
                    .graduation(Math.round(graduationRate * scale * 10) / 10.0)
                    .build());
        }
        return semesters;
    }

    private List<ChartPoint> buildActivityByMonth(DepartmentDashboardDto deptDashboard,
                                                    Map<String, Double> eventsByMonth,
                                                    double pubsPerMonth) {
        // Collect all month keys from projects and events
        Set<String> allMonths = new LinkedHashSet<>();
        if (deptDashboard != null && deptDashboard.getProjectsByMonth() != null) {
            allMonths.addAll(deptDashboard.getProjectsByMonth().keySet());
        }
        allMonths.addAll(eventsByMonth.keySet());

        if (allMonths.isEmpty()) return Collections.emptyList();

        Map<String, Integer> projectsByMonth = deptDashboard != null && deptDashboard.getProjectsByMonth() != null
                ? deptDashboard.getProjectsByMonth() : Collections.emptyMap();

        return allMonths.stream()
                .map(month -> ChartPoint.builder()
                        .name(month)
                        .projects((double) projectsByMonth.getOrDefault(month, 0))
                        .events(eventsByMonth.getOrDefault(month, 0.0))
                        .publications(pubsPerMonth)
                        .build())
                .collect(Collectors.toList());
    }

    private Map<String, Double> safeGetDepartmentEventsByMonth(UUID departmentId) {
        try {
            List<EventDto> allEvents = eventServiceClient.getAllEvents();
            if (allEvents == null) return Collections.emptyMap();

            YearMonth now = YearMonth.now();
            Locale ru = new Locale("ru");

            return allEvents.stream()
                    .filter(e -> departmentId.equals(e.getDepartmentId()) && e.getStartDate() != null)
                    .filter(e -> {
                        YearMonth ym = YearMonth.from(e.getStartDate());
                        return !ym.isBefore(now.minusMonths(5)) && !ym.isAfter(now);
                    })
                    .collect(Collectors.groupingBy(
                            e -> YearMonth.from(e.getStartDate()).getMonth()
                                    .getDisplayName(TextStyle.SHORT, ru),
                            Collectors.summingDouble(e -> 1.0)));
        } catch (Exception e) {
            log.warn("Failed to get department events: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    private double safeGetDepartmentPublicationsPerMonth(UUID departmentId) {
        try {
            List<User> lecturers = userRepository.findByDepartmentIdAndRoleAndIsActiveTrue(departmentId, UserRole.LECTURER);
            if (lecturers.isEmpty()) return 0.0;

            List<UUID> lecturerIds = lecturers.stream().map(User::getId).collect(Collectors.toList());
            List<LecturerAcademicMetrics> metrics = metricsRepository.findByUserIdIn(lecturerIds);

            int totalPublications = metrics.stream().mapToInt(LecturerAcademicMetrics::getPublications).sum();
            return Math.round(totalPublications / 12.0 * 10) / 10.0;
        } catch (Exception e) {
            log.warn("Failed to get department publications: {}", e.getMessage());
            return 0.0;
        }
    }

    // ==================== KPI builders (legacy) ====================

    private List<KpiItem> buildTeacherKpis(Map<String, Integer> projectStats, Map<String, Integer> taskStats) {
        return List.of(
                KpiItem.builder()
                        .label("Активных проектов")
                        .value(projectStats.getOrDefault("activeCount", 0))
                        .delta("")
                        .iconTone("blue")
                        .build(),
                KpiItem.builder()
                        .label("Задач на проверке")
                        .value(taskStats.getOrDefault("reviewCount", 0))
                        .delta("")
                        .iconTone("green")
                        .build(),
                KpiItem.builder()
                        .label("Всего задач")
                        .value(taskStats.getOrDefault("totalCount", 0))
                        .delta("")
                        .iconTone("orange")
                        .build()
        );
    }

    // ==================== Safe cross-service calls ====================

    private Map<String, Integer> safeGetProjectStats(UUID userId) {
        try {
            return getProjectStats(userId);
        } catch (Exception e) {
            log.warn("Failed to get project stats for user {}: {}", userId, e.getMessage());
            return Collections.emptyMap();
        }
    }

    private Map<String, Integer> safeGetTaskStats(UUID userId) {
        try {
            return getTaskStats(userId);
        } catch (Exception e) {
            log.warn("Failed to get task stats for user {}: {}", userId, e.getMessage());
            return Collections.emptyMap();
        }
    }

    private ProjectDashboardStatsDto safeGetDetailedStats(UUID userId) {
        try {
            var response = projectServiceClient.getUserProjectDetailedStats(userId);
            return response != null && response.getData() != null ? response.getData() : null;
        } catch (Exception e) {
            log.warn("Failed to get detailed stats for user {}: {}", userId, e.getMessage());
            return null;
        }
    }

    private List<TaskDto> safeGetRecentTasks(UUID userId) {
        try {
            var response = projectServiceClient.getRecentUserTasks(userId);
            return response != null && response.getData() != null ? response.getData() : Collections.emptyList();
        } catch (Exception e) {
            log.warn("Failed to get recent tasks for user {}: {}", userId, e.getMessage());
            return Collections.emptyList();
        }
    }

    private DepartmentDashboardDto safeGetDepartmentDashboard(UUID departmentId) {
        try {
            var response = projectServiceClient.getDepartmentDashboard(departmentId);
            return response != null && response.getData() != null ? response.getData() : null;
        } catch (Exception e) {
            log.warn("Failed to get department dashboard for {}: {}", departmentId, e.getMessage());
            return null;
        }
    }

    private Map<String, Object> safeGetStudentRating(UUID userId) {
        try {
            return ratingServiceClient.getStudentRating(userId);
        } catch (Exception e) {
            log.warn("Failed to get student rating for {}: {}", userId, e.getMessage());
            return null;
        }
    }

    @CircuitBreaker(name = "projectService", fallbackMethod = "fallbackProjectStats")
    public Map<String, Integer> getProjectStats(UUID userId) {
        var response = projectServiceClient.getUserProjectStats(userId);
        return response != null && response.getData() != null ? response.getData() : Collections.emptyMap();
    }

    @CircuitBreaker(name = "projectService", fallbackMethod = "fallbackTaskStats")
    public Map<String, Integer> getTaskStats(UUID userId) {
        var response = projectServiceClient.getUserTaskStats(userId);
        return response != null && response.getData() != null ? response.getData() : Collections.emptyMap();
    }

    @SuppressWarnings("unused")
    public Map<String, Integer> fallbackProjectStats(UUID userId, Throwable t) {
        log.warn("Circuit breaker fallback for project stats: {}", t.getMessage());
        return Collections.emptyMap();
    }

    @SuppressWarnings("unused")
    public Map<String, Integer> fallbackTaskStats(UUID userId, Throwable t) {
        log.warn("Circuit breaker fallback for task stats: {}", t.getMessage());
        return Collections.emptyMap();
    }

    // ==================== Utility methods ====================

    private String timeAgo(OffsetDateTime dateTime) {
        if (dateTime == null) return "";
        long minutes = ChronoUnit.MINUTES.between(dateTime, OffsetDateTime.now());
        if (minutes < 1) return "только что";
        if (minutes < 60) return minutes + " мин. назад";
        long hours = minutes / 60;
        if (hours < 24) return hours + " ч. назад";
        long days = hours / 24;
        if (days < 7) return days + " дн. назад";
        return formatDate(dateTime);
    }

    private String formatDate(OffsetDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.toLocalDate().toString();
    }

    private String extractInitials(String text) {
        if (text == null || text.isBlank()) return "ИИ";
        String[] parts = text.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(2, parts.length); i++) {
            if (!parts[i].isEmpty()) sb.append(Character.toUpperCase(parts[i].charAt(0)));
        }
        return sb.length() > 0 ? sb.toString() : "ИИ";
    }

    private String formatName(String firstName, String lastName) {
        if (firstName == null && lastName == null) return "Неизвестный";
        return ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
    }

    private String notificationTone(NotificationType type) {
        if (type == null) return "blue";
        return switch (type) {
            case TASK_ASSIGNED -> "blue";
            case DEADLINE -> "orange";
            case PROJECT_INVITE -> "purple";
            case EVENT_UPDATE -> "green";
            case RATING_UPDATE -> "yellow";
            case SYSTEM -> "gray";
        };
    }

    private String notificationTypeLabel(NotificationType type) {
        if (type == null) return "";
        return switch (type) {
            case TASK_ASSIGNED -> "Задача";
            case DEADLINE -> "Дедлайн";
            case PROJECT_INVITE -> "Приглашение";
            case EVENT_UPDATE -> "Мероприятие";
            case RATING_UPDATE -> "Рейтинг";
            case SYSTEM -> "Система";
        };
    }
}
