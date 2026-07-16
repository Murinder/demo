package com.example.eventservice.repository;

import com.example.eventservice.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    List<Lesson> findByUserIdAndSemester(UUID userId, Integer semester);
    List<Lesson> findByUserId(UUID userId);
    List<Lesson> findByGroupNameAndSemester(String groupName, Integer semester);
}
