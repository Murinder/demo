package com.example.projectservice.repository;

import com.example.projectservice.model.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository для ProjectMember сущности
 */
@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMember.ProjectMemberId> {
    List<ProjectMember> findById_ProjectId(UUID projectId);

    List<ProjectMember> findById_UserId(UUID userId);

    /**
     * Count distinct students (non-MENTOR members) across all projects
     * where the given user is a MENTOR.
     */
    @Query(value = """
        SELECT COUNT(DISTINCT m2.user_id)
        FROM project_members m1
        JOIN project_members m2 ON m1.project_id = m2.project_id
        WHERE m1.user_id = :mentorId
          AND m1.role = 'MENTOR'
          AND m2.user_id <> :mentorId
          AND m2.role <> 'MENTOR'
        """, nativeQuery = true)
    long countStudentsByMentor(@Param("mentorId") UUID mentorId);

    /**
     * Count distinct students across ACTIVE projects where the given user is a MENTOR.
     */
    @Query(value = """
        SELECT COUNT(DISTINCT m2.user_id)
        FROM project_members m1
        JOIN project_members m2 ON m1.project_id = m2.project_id
        JOIN projects p ON p.id = m1.project_id
        WHERE m1.user_id = :mentorId
          AND m1.role = 'MENTOR'
          AND m2.user_id <> :mentorId
          AND m2.role <> 'MENTOR'
          AND p.status = 'ACTIVE'
        """, nativeQuery = true)
    long countActiveStudentsByMentor(@Param("mentorId") UUID mentorId);
}