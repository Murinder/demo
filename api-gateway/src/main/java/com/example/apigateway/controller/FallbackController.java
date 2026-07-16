package com.example.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping("/fallback")
    public ResponseEntity<Map<String, Object>> fallbackGet() {
        return fallbackResponse();
    }

    @PostMapping("/fallback")
    public ResponseEntity<Map<String, Object>> fallbackPost() {
        return fallbackResponse();
    }

    private ResponseEntity<Map<String, Object>> fallbackResponse() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "success", false,
                        "code", "SERVICE_UNAVAILABLE",
                        "message", "Service is temporarily unavailable. Please try again later."
                ));
    }
}