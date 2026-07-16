package com.example.analyticsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name = "project-service")
public interface ProjectServiceClient {

    @GetMapping("/api/v1/projects")
    List<Map<String, Object>> getProjects();
}
