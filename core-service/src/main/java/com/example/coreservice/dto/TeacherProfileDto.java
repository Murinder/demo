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
public class TeacherProfileDto {
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

    // Awards
    private List<HeadProfileDto.AwardItem> awards;
}
