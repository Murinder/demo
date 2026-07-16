package com.example.analyticsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name = "event-service")
public interface EventServiceClient {

    @GetMapping("/api/v1/events")
    List<Map<String, Object>> getEvents();
}
