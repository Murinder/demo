package com.example.projectservice.repository;

import com.example.projectservice.model.ProjectDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository для ProjectDocument сущности
 */
@Repository
public interface ProjectDocumentRepository extends JpaRepository<ProjectDocument, UUID> {
    List<ProjectDocument> findByProjectId(UUID projectId);
    List<ProjectDocument> findByTaskId(UUID taskId);
}