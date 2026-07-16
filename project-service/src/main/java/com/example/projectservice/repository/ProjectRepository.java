package com.example.projectservice.repository;

import com.example.projectservice.model.Project;
import com.example.sharedlib.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository для Project сущности
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByCreatedBy(UUID createdBy);
    List<Project> findByStatus(ProjectStatus status);
    List<Project> findByDepartmentId(UUID departmentId);
    List<Project> findByStatusAndDepartmentId(ProjectStatus status, UUID departmentId);
}