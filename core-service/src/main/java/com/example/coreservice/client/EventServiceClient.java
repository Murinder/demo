package com.example.coreservice.client;

import com.example.sharedlib.dto.EventDto;
import com.example.sharedlib.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "event-service")
public interface EventServiceClient {

    @GetMapping("/api/v1/events")
    List<EventDto> getAllEvents();

    @GetMapping("/api/v1/events/defenses")
    ApiResponse<List<Map<String, Object>>> getDefensesBySupervisor(@RequestParam("supervisorId") UUID supervisorId);
}
