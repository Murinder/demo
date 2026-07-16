package com.example.coreservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSkillId {
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "skill_name")
    private String skillName;
}