package com.example.projectservice.repository;

import com.example.projectservice.model.ProjectTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository для ProjectTemplate сущности
 */
@Repository
public interface ProjectTemplateRepository extends JpaRepository<ProjectTemplate, UUID> {
}