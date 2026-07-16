package com.example.eventservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application class for Event Service
 */
@SpringBootApplication(scanBasePackages = {"com.example.eventservice", "com.example.sharedlib"})
@EnableDiscoveryClient
@EnableFeignClients
public class EventServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventServiceApplication.class, args);
    }
}

