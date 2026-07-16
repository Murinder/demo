package com.example.projectservice.service;

import com.example.projectservice.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserNameCacheService {

    private final UserServiceClient userServiceClient;

    @SuppressWarnings("unchecked")
    @Cacheable(value = "userNames", key = "#userId")
    public String fetchUserName(UUID userId) {
        try {
            Map<String, Object> response = userServiceClient.getUserProfileRaw(userId);
            Object data = response.get("data");
            if (data instanceof Map) {
                Map<String, Object> profile = (Map<String, Object>) data;
                String first = (String) profile.getOrDefault("firstName", "");
                String last = (String) profile.getOrDefault("lastName", "");
                String name = (first + " " + last).trim();
                return name.isEmpty() ? null : name;
            }
        } catch (Exception e) {
            log.warn("Could not fetch profile for user {}: {}", userId, e.getMessage());
        }
        return null;
    }
}
