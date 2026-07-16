package com.example.coreservice.repository;

import com.example.coreservice.model.entity.User;
import com.example.sharedlib.enums.UserRole;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository для User сущности
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByIdAndIsActiveTrue(UUID id);
    List<User> findByRoleAndIsActiveTrue(UserRole role);
    List<User> findByDepartmentIdAndRoleAndIsActiveTrue(UUID departmentId, UserRole role);
    long countByDepartmentIdAndRoleAndIsActiveTrue(UUID departmentId, UserRole role);
    List<User> findByGroupNameAndRoleAndIsActiveTrue(String groupName, UserRole role);
    List<User> findByFacultyIdAndRoleAndIsActiveTrue(UUID facultyId, UserRole role);

    @Query("SELECT DISTINCT u.groupName FROM User u WHERE u.groupName IS NOT NULL AND u.isActive = true ORDER BY u.groupName")
    List<String> findDistinctGroupNames();

    @Query("SELECT u FROM User u WHERE u.isActive = true " +
           "AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(CONCAT(u.lastName, ' ', u.firstName)) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<User> searchByName(@Param("query") String query, Pageable pageable);
}

