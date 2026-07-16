package com.example.coreservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "rating-service")
public interface RatingServiceClient {

    @GetMapping("/api/v1/ratings/students/{userId}")
    Map<String, Object> getStudentRating(@PathVariable UUID userId);

    @GetMapping("/api/v1/ratings/lecturers/{userId}")
    Map<String, Object> getLecturerRating(@PathVariable UUID userId);
}
