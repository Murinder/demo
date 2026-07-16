package com.example.coreservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Aggregated dashboard data matching the React ActivityPage mock data shapes.
 * Contains role-specific sections.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSummaryDto {

    private String role; // STUDENT, LECTURER, DEPARTMENT_HEAD

    // Teacher KPIs
    private List<KpiItem> kpis;
    private List<ChartPoint> weeklyChart;       // { name, created, done }
    private List<ChartPoint> studentProgress;   // { name, value }
    private List<ActivityItem> lastActivity;
    private List<StudentItem> activeStudents;
    private List<AttentionProject> attentionProjects;

    // Student KPIs
    private List<SimpleKpi> studentKpis;
    private List<ChartPoint> barData;           // monthly progress
    private List<PieSlice> pieData;             // time distribution
    private List<ActivityLogItem> activities;
    private List<SubjectItem> subjectsData;

    // Head KPIs
    private List<KpiItem> headKpis;
    private List<GroupPerformance> performanceByGroup;
    private List<PieSlice> studentsByCourse;
    private List<ChartPoint> semesters;
    private List<ChartPoint> activityByMonth;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class KpiItem {
        private String label;
        private double value;
        private String delta;
        private String icon;
        private String iconTone;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SimpleKpi {
        private String title;
        private String value;
        private String sub;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChartPoint {
        private String name;
        private Double value;
        private Double created;
        private Double done;
        private Double graduation;
        private Double avgGrade;
        private Double projects;
        private Double events;
        private Double publications;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PieSlice {
        private String name;
        private int value;
        private String color;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ActivityItem {
        private String id;
        private String initials;
        private String name;
        private String actionTitle;
        private String actionSub;
        private String timeAgo;
        private String tone;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudentItem {
        private String id;
        private String initials;
        private String name;
        private String project;
        private int tasksDone;
        private int tasksTotal;
        private String lastActive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttentionProject {
        private String id;
        private String title;
        private String statusLabel;
        private String statusTone;
        private String members;
        private String issue;
        private String issueTone;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ActivityLogItem {
        private String user;
        private String action;
        private String date;
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubjectItem {
        private String name;
        private int grade;
        private int attendance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GroupPerformance {
        private String name;
        private int activity;
        private int attendance;
        private double avgGrade;
    }
}
