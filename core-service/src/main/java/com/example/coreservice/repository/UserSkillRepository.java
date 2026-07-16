package com.example.coreservice.repository;

import com.example.coreservice.model.entity.UserSkill;
import com.example.coreservice.model.entity.UserSkillId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserSkillRepository extends JpaRepository<UserSkill, UserSkillId> {
    List<UserSkill> findByIdUserId(UUID userId);
}