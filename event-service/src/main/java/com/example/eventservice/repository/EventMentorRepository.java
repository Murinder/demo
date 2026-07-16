package com.example.eventservice.repository;

import com.example.eventservice.model.EventMentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventMentorRepository extends JpaRepository<EventMentor, EventMentor.EventMentorId> {
}