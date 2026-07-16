package com.example.coreservice.repository;

import com.example.coreservice.model.entity.UserLink;
import com.example.coreservice.model.entity.UserLinkId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserLinkRepository extends JpaRepository<UserLink, UserLinkId> {
    List<UserLink> findByIdUserId(UUID userId);
}