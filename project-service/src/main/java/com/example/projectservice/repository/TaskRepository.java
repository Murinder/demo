package com.example.projectservice.repository;

import com.example.projectservice.model.Task;
import com.example.sharedlib.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository для Task сущности
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByProjectId(UUID projectId);
    List<Task> findByAssignedTo(UUID assignedTo);
    List<Task> findByProjectIdAndStatus(UUID projectId, TaskStatus status);
    List<Task> findByAssignedToAndStatus(UUID assignedTo, TaskStatus status);
    List<Task> findTop10ByAssignedToOrderByUpdatedAtDesc(UUID assignedTo);
    long countByProjectId(UUID projectId);
    long countByProjectIdAndStatus(UUID projectId, TaskStatus status);
}

