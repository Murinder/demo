package com.example.coreservice.repository;

import com.example.coreservice.model.entity.UserAward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserAwardRepository extends JpaRepository<UserAward, UUID> {
    List<UserAward> findByUserId(UUID userId);
}
