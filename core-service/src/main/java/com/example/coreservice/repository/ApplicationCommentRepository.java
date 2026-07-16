package com.example.coreservice.repository;

import com.example.coreservice.model.entity.ApplicationComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationCommentRepository extends JpaRepository<ApplicationComment, UUID> {
    List<ApplicationComment> findByApplicationIdOrderByCreatedAtAsc(UUID applicationId);
}
