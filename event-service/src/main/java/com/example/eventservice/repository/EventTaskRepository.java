package com.example.eventservice.repository;

import com.example.eventservice.model.EventTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventTaskRepository extends JpaRepository<EventTask, UUID> {
}