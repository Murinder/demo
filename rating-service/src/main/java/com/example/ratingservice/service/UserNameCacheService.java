package com.example.ratingservice.service;

import com.example.ratingservice.client.CoreServiceClient;
import com.example.sharedlib.dto.UserProfileDto;
import com.example.sharedlib.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserNameCacheService {

    private final CoreServiceClient coreServiceClient;

    @Cacheable(value = "userNames", key = "#userId")
    public String fetchUserName(UUID userId) {
        try {
            ApiResponse<UserProfileDto> resp = coreServiceClient.getUserProfile(userId);
            if (resp != null && resp.isSuccess() && resp.getData() != null) {
                UserProfileDto p = resp.getData();
                String first = p.getFirstName() != null ? p.getFirstName() : "";
                String last = p.getLastName() != null ? p.getLastName() : "";
                String name = (first + " " + last).trim();
                return name.isEmpty() ? null : name;
            }
        } catch (Exception e) {
            log.warn("Could not fetch profile for user {}: {}", userId, e.getMessage());
        }
        return null;
    }
}
