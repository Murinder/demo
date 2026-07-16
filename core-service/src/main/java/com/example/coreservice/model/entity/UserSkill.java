package com.example.coreservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;



@Entity
@Table(name = "user_skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSkill {
    @EmbeddedId
    private UserSkillId id;

    @Column(name = "level", nullable = false)
    private int level;

    @Column(name = "verified", nullable = false)
    private boolean verified = false;
}