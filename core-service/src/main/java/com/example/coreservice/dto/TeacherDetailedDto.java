package com.example.coreservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDetailedDto {
    private String id;
    private String name;
    private String title;
    private double rating;
    private int activeProjects;
    private int totalProjects;
    private int students;
    private int completion;
    private double avgGrade;
    private int publications;
    private int grants;
    private int hours;
    private int consultations;
    private List<ActivityPoint> activity;
    private List<SuccessPoint> success;
    private ProjectsStats projectsStats;
    private StudentsStats studentsStats;
    private ScienceStats scienceStats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityPoint {
        private String name;
        private int consultations;
        private int projects;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuccessPoint {
        private String name;
        private int value;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectsStats {
        private int active;
        private int done;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentsStats {
        private int total;
        private int active;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScienceStats {
        private int publications;
        private int grants;
    }
}
