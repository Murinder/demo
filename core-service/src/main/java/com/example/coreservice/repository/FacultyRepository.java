package com.example.coreservice.repository;

import com.example.coreservice.model.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, UUID> {
    Optional<Faculty> findByCode(String code);
    boolean existsByCode(String code);
}
