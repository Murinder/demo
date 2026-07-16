package com.example.coreservice.repository;

import com.example.coreservice.model.entity.UserLanguage;
import com.example.coreservice.model.entity.UserLanguageId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserLanguageRepository extends JpaRepository<UserLanguage, UserLanguageId> {
    List<UserLanguage> findByIdUserId(UUID userId);
}