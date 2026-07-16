package com.example.coreservice.controller;

import com.example.coreservice.dto.NotificationSettingDto;
import com.example.coreservice.service.NotificationSettingService;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notification-settings")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Notification Settings", description = "Notification setting management APIs")
public class NotificationSettingController {
    private final NotificationSettingService notificationSettingService;

    @GetMapping
    @Operation(summary = "Get all notification settings")
    public ResponseEntity<List<NotificationSettingDto>> getAll() {
        return ResponseEntity.ok(notificationSettingService.getAllNotificationSettings());
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get notification setting by user id")
    public ResponseEntity<NotificationSettingDto> getByUserId(@PathVariable UUID userId) {
        NotificationSettingDto notificationSettingDto = notificationSettingService.getNotificationSettingByUserId(userId);
        if (notificationSettingDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(notificationSettingDto);
    }

    @PostMapping
    @Operation(summary = "Create a new notification setting")
    public ResponseEntity<NotificationSettingDto> create(@RequestBody NotificationSettingDto setting) {
        return ResponseEntity.ok(notificationSettingService.createNotificationSetting(setting));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update a notification setting")
    public ResponseEntity<NotificationSettingDto> update(@PathVariable UUID userId, @RequestBody NotificationSettingDto setting) {
        NotificationSettingDto updatedSetting = notificationSettingService.updateNotificationSetting(userId, setting);
        if (updatedSetting == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedSetting);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete a notification setting")
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        notificationSettingService.deleteNotificationSetting(userId);
        return ResponseEntity.noContent().build();
    }
}