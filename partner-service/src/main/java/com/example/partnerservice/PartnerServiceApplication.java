package com.example.partnerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main application class for Partner Service
 */
@SpringBootApplication(scanBasePackages = {"com.example.partnerservice", "com.example.sharedlib"})
@EnableDiscoveryClient
public class PartnerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PartnerServiceApplication.class, args);
    }
}

