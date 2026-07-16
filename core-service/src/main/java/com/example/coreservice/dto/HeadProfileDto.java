package com.example.coreservice.dto;

import com.example.sharedlib.dto.UserProfileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeadProfileDto {
    private UserProfileDto profile;

    // From LecturerAcademicMetrics
    private int publications;
    private int monographs;
    private int articles;
    private int conferences;
    private int grants;
    private int hours;
    private int consultations;
    private Integer teachingStartYear;
    private int supervisedPhd;
    private int supervisedMasters;
    private int supervisedBachelors;

    // Computed from department
    private int departmentTeacherCount;
    private int departmentStudentCount;

    // Awards
    private List<AwardItem> awards;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AwardItem {
        private String id;
        private String title;
        private String year;
    }
}
