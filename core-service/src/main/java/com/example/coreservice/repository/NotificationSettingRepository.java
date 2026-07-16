package com.example.coreservice.repository;

import com.example.coreservice.model.entity.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, UUID> {
}