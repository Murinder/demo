package com.example.analyticsservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI analyticsServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ETSOPY Analytics Service API")
                        .description("Сервис аналитики платформы ETSOPY: генерация отчётов по проектам и мероприятиям, "
                                + "KPI проектов, кафедр и платформы в целом, управление компетенциями.")
                        .version("1.0.0"));
    }
}
