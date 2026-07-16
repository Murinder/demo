package com.example.projectservice.client;

import com.example.sharedlib.dto.UserProfileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "core-service")
public interface UserServiceClient {

    @GetMapping("/api/v1/auth/profile/{userId}")
    UserProfileDto getUserProfile(@PathVariable UUID userId);

    @GetMapping("/api/v1/auth/profile/{userId}")
    Map<String, Object> getUserProfileRaw(@PathVariable UUID userId);
}
