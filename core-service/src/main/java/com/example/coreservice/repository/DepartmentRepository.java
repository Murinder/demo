package com.example.coreservice.repository;

import com.example.coreservice.model.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    List<Department> findByFacultyId(UUID facultyId);
    Optional<Department> findByCode(String code);
    boolean existsByCode(String code);
}
