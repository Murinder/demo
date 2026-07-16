package com.example.coreservice.repository;

import com.example.coreservice.model.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
}