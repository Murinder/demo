package com.example.portfolioservice.repository;

import com.example.portfolioservice.model.AcademicInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AcademicInfoRepository extends JpaRepository<AcademicInfo, UUID> {
}
