package com.example.coreservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Core Service API",
                version = "v1",
                description = "Базовый сервис платформы ETSOPY: аутентификация (JWT), управление пользователями и профилями, "
                        + "чаты и сообщения, уведомления, дашборды, поиск, факультеты, кафедры, "
                        + "интеграции с Telegram и SSO, экспорт календаря (iCal)."
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}