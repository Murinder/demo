package com.example.portfolioservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI portfolioServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ETSOPY Portfolio Service API")
                        .description("Сервис портфолио платформы ETSOPY: управление портфолио студентов и преподавателей, "
                                + "навыки, достижения, отзывы, поиск портфолио.")
                        .version("1.0.0"));
    }
}
