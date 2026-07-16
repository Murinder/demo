package com.example.apigateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayOpenApiConfig {

    @Bean
    public OpenAPI gatewayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ETSOPY - Unified Digital Educational Platform")
                        .version("1.0.0")
                        .description("""
                                ## ETSOPY Platform API Gateway

                                Единая цифровая образовательная платформа университета (ETSOPY) — это микросервисная система, \
                                объединяющая управление проектами, мероприятиями, портфолио студентов, аналитику, рейтинги, \
                                партнёрские интеграции и документооборот.

                                ### Архитектура системы

                                Все запросы к платформе проходят через **API Gateway** (данный сервис), который выполняет:
                                - **Маршрутизацию** запросов к соответствующим микросервисам
                                - **JWT-аутентификацию** и валидацию токенов
                                - **Rate Limiting** для защиты от перегрузок
                                - **Circuit Breaker** для обеспечения отказоустойчивости
                                - **Балансировку нагрузки** через Eureka Service Discovery

                                ### Микросервисы платформы

                                | Сервис | Описание | Основные ресурсы |
                                |--------|----------|-----------------|
                                | **Core Service** | Ядро платформы | Аутентификация, пользователи, чаты, уведомления, дашборды, факультеты, кафедры |
                                | **Project Service** | Проектная деятельность | Проекты, задачи (Kanban), участники, документы, шаблоны |
                                | **Event Service** | Мероприятия | События, команды, заявки, задачи мероприятий, менторы |
                                | **Portfolio Service** | Портфолио | Навыки, достижения, отзывы, поиск портфолио |
                                | **Analytics Service** | Аналитика | Отчёты, KPI проектов/кафедр/платформы, компетенции |
                                | **Rating Service** | Рейтинги | Оценки студентов, преподавателей, кафедр, критерии |
                                | **Partner Service** | Партнёрство | Организации-партнёры, кейсы, вакансии, соглашения |
                                | **Admin Service** | Администрирование | Управление пользователями, роли, аудит, настройки системы |
                                | **Document Service** | Документооборот | Шаблоны документов, генерация, цифровые подписи |

                                ### Аутентификация

                                Платформа использует **JWT Bearer Token** аутентификацию.

                                1. Получите токен через `POST /api/v1/auth/login`
                                2. Добавьте заголовок: `Authorization: Bearer <token>`
                                3. Публичные эндпоинты (не требуют токен): `/api/v1/auth/login`, `/api/v1/auth/register`

                                Токен автоматически валидируется на уровне API Gateway и преобразуется \
                                в заголовки `X-User-Id` и `X-User-Role` для микросервисов.

                                ### Навигация

                                Выберите нужный микросервис в выпадающем списке **Select a definition** выше \
                                для просмотра его API-эндпоинтов.
                                """)
                        .contact(new Contact()
                                .name("ETSOPY Team")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
