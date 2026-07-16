package com.example.sharedlib.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configures MinIO FileStorageService when minio properties are set.
 */
@Configuration
@ConditionalOnClass(name = "io.minio.MinioClient")
@ConditionalOnProperty(prefix = "minio", name = "endpoint")
public class FileStorageAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(FileStorageService.class)
    public FileStorageService fileStorageService(
            @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey) {
        return new MinioFileStorageService(endpoint, accessKey, secretKey);
    }
}