package com.example.coreservice.repository;

import com.example.coreservice.model.entity.Application;
import com.example.coreservice.model.enums.ApplicationKind;
import com.example.coreservice.model.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findByStudentIdOrderBySubmittedAtDesc(UUID studentId);
    List<Application> findByLecturerIdOrderBySubmittedAtDesc(UUID lecturerId);
    List<Application> findByStatusInOrderBySubmittedAtDesc(List<ApplicationStatus> statuses);
    List<Application> findByKindInAndStatusInOrderBySubmittedAtDesc(List<ApplicationKind> kinds, List<ApplicationStatus> statuses);
}
