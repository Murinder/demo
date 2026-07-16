package com.example.documentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application class for Document Service
 */
@SpringBootApplication(scanBasePackages = {"com.example.documentservice", "com.example.sharedlib"})
@EnableDiscoveryClient
@EnableFeignClients
public class DocumentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentServiceApplication.class, args);
    }
}
