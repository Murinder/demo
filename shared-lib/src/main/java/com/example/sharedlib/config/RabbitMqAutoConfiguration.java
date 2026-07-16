package com.example.sharedlib.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration for RabbitMQ exchanges, queues, and bindings.
 * Activated when spring-amqp is on the classpath.
 */
@Configuration
@ConditionalOnClass(TopicExchange.class)
public class RabbitMqAutoConfiguration {

    // ==================== EXCHANGES ====================

    public static final String USER_EXCHANGE = "etsopy.user";
    public static final String PROJECT_EXCHANGE = "etsopy.project";
    public static final String EVENT_EXCHANGE = "etsopy.event";
    public static final String RATING_EXCHANGE = "etsopy.rating";
    public static final String NOTIFICATION_EXCHANGE = "etsopy.notification";
    public static final String SEARCH_EXCHANGE = "etsopy.search";
    public static final String PORTFOLIO_EXCHANGE = "etsopy.portfolio";
    public static final String PARTNER_EXCHANGE = "etsopy.partner";
    public static final String DOCUMENT_EXCHANGE = "etsopy.document";

    // ==================== ROUTING KEYS ====================

    public static final String USER_CREATED_KEY = "user.created";
    public static final String USER_UPDATED_KEY = "user.updated";
    public static final String USER_ROLE_CHANGED_KEY = "user.role_changed";

    public static final String PROJECT_CREATED_KEY = "project.created";
    public static final String PROJECT_STATUS_CHANGED_KEY = "project.status_changed";
    public static final String PROJECT_MEMBER_ADDED_KEY = "project.member_added";
    public static final String PROJECT_MEMBER_REMOVED_KEY = "project.member_removed";
    public static final String PROJECT_TASK_COMPLETED_KEY = "project.task_completed";
    public static final String PROJECT_DOCUMENT_UPLOADED_KEY = "project.document_uploaded";

    public static final String EVENT_CREATED_KEY = "event.created";
    public static final String EVENT_STATUS_CHANGED_KEY = "event.status_changed";
    public static final String EVENT_APPLICATION_SUBMITTED_KEY = "event.application_submitted";
    public static final String EVENT_APPLICATION_DECIDED_KEY = "event.application_decided";
    public static final String EVENT_TEAM_FORMED_KEY = "event.team_formed";
    public static final String EVENT_COMPLETED_KEY = "event.completed";

    public static final String RATING_RECALCULATED_KEY = "rating.recalculated";

    public static final String NOTIFICATION_SEND_KEY = "notification.send";

    public static final String DOCUMENT_GENERATED_KEY = "document.generated";

    public static final String PARTNER_CASE_PUBLISHED_KEY = "partner.case_published";
    public static final String PARTNER_VACANCY_PUBLISHED_KEY = "partner.vacancy_published";

    public static final String PORTFOLIO_ACHIEVEMENT_ADDED_KEY = "portfolio.achievement_added";

    // ==================== QUEUE NAMES ====================

    public static final String QUEUE_USER_CREATED_PORTFOLIO = "user.created.portfolio";
    public static final String QUEUE_USER_CREATED_SEARCH = "user.created.search";
    public static final String QUEUE_USER_CREATED_RATING = "user.created.rating";
    public static final String QUEUE_USER_UPDATED_SEARCH = "user.updated.search";

    public static final String QUEUE_PROJECT_CREATED_CHAT = "project.created.chat";
    public static final String QUEUE_PROJECT_CREATED_RATING = "project.created.rating";
    public static final String QUEUE_PROJECT_CREATED_SEARCH = "project.created.search";
    public static final String QUEUE_PROJECT_STATUS_RATING = "project.status_changed.rating";
    public static final String QUEUE_PROJECT_STATUS_ANALYTICS = "project.status_changed.analytics";
    public static final String QUEUE_PROJECT_STATUS_SEARCH = "project.status_changed.search";
    public static final String QUEUE_PROJECT_MEMBER_ADDED_CHAT = "project.member_added.chat";
    public static final String QUEUE_PROJECT_MEMBER_ADDED_RATING = "project.member_added.rating";
    public static final String QUEUE_PROJECT_MEMBER_ADDED_PORTFOLIO = "project.member_added.portfolio";
    public static final String QUEUE_PROJECT_MEMBER_REMOVED_CHAT = "project.member_removed.chat";
    public static final String QUEUE_PROJECT_TASK_COMPLETED_RATING = "project.task_completed.rating";
    public static final String QUEUE_PROJECT_TASK_COMPLETED_ANALYTICS = "project.task_completed.analytics";
    public static final String QUEUE_PROJECT_DOCUMENT_UPLOADED_SEARCH = "project.document_uploaded.search";

    public static final String QUEUE_EVENT_CREATED_SEARCH = "event.created.search";
    public static final String QUEUE_EVENT_CREATED_NOTIFICATION = "event.created.notification";
    public static final String QUEUE_EVENT_STATUS_NOTIFICATION = "event.status_changed.notification";
    public static final String QUEUE_EVENT_STATUS_SEARCH = "event.status_changed.search";
    public static final String QUEUE_EVENT_APPLICATION_NOTIFICATION = "event.application_submitted.notification";
    public static final String QUEUE_EVENT_DECIDED_NOTIFICATION = "event.application_decided.notification";
    public static final String QUEUE_EVENT_DECIDED_RATING = "event.application_decided.rating";
    public static final String QUEUE_EVENT_TEAM_NOTIFICATION = "event.team_formed.notification";
    public static final String QUEUE_EVENT_COMPLETED_RATING = "event.completed.rating";
    public static final String QUEUE_EVENT_COMPLETED_PORTFOLIO = "event.completed.portfolio";
    public static final String QUEUE_EVENT_COMPLETED_ANALYTICS = "event.completed.analytics";

    public static final String QUEUE_RATING_RECALCULATED_CORE = "rating.recalculated.core";
    public static final String QUEUE_RATING_RECALCULATED_PORTFOLIO = "rating.recalculated.portfolio";

    public static final String QUEUE_NOTIFICATION_SEND = "notification.send.core";

    public static final String QUEUE_DOCUMENT_GENERATED_NOTIFICATION = "document.generated.notification";

    public static final String QUEUE_PARTNER_CASE_SEARCH = "partner.case_published.search";
    public static final String QUEUE_PARTNER_VACANCY_SEARCH = "partner.vacancy_published.search";

    public static final String QUEUE_PORTFOLIO_ACHIEVEMENT_RATING = "portfolio.achievement_added.rating";
    public static final String QUEUE_PORTFOLIO_ACHIEVEMENT_SEARCH = "portfolio.achievement_added.search";

    // ==================== MESSAGE CONVERTER ====================

    @Bean
    public MessageConverter jackson2JsonMessageConverter(com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    // ==================== EXCHANGES ====================

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    @Bean
    public TopicExchange projectExchange() {
        return new TopicExchange(PROJECT_EXCHANGE);
    }

    @Bean
    public TopicExchange eventExchange() {
        return new TopicExchange(EVENT_EXCHANGE);
    }

    @Bean
    public TopicExchange ratingExchange() {
        return new TopicExchange(RATING_EXCHANGE);
    }

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public TopicExchange searchExchange() {
        return new TopicExchange(SEARCH_EXCHANGE);
    }

    @Bean
    public TopicExchange portfolioExchange() {
        return new TopicExchange(PORTFOLIO_EXCHANGE);
    }

    @Bean
    public TopicExchange partnerExchange() {
        return new TopicExchange(PARTNER_EXCHANGE);
    }

    @Bean
    public TopicExchange documentExchange() {
        return new TopicExchange(DOCUMENT_EXCHANGE);
    }

    // ==================== QUEUES ====================

    @Bean public Queue queueNotificationSend() { return new Queue(QUEUE_NOTIFICATION_SEND, true); }
    @Bean public Queue queueUserCreatedPortfolio() { return new Queue(QUEUE_USER_CREATED_PORTFOLIO, true); }
    @Bean public Queue queueUserCreatedSearch() { return new Queue(QUEUE_USER_CREATED_SEARCH, true); }
    @Bean public Queue queueUserCreatedRating() { return new Queue(QUEUE_USER_CREATED_RATING, true); }
    @Bean public Queue queueUserUpdatedSearch() { return new Queue(QUEUE_USER_UPDATED_SEARCH, true); }
    @Bean public Queue queueProjectCreatedChat() { return new Queue(QUEUE_PROJECT_CREATED_CHAT, true); }
    @Bean public Queue queueProjectCreatedRating() { return new Queue(QUEUE_PROJECT_CREATED_RATING, true); }
    @Bean public Queue queueProjectCreatedSearch() { return new Queue(QUEUE_PROJECT_CREATED_SEARCH, true); }
    @Bean public Queue queueProjectStatusRating() { return new Queue(QUEUE_PROJECT_STATUS_RATING, true); }
    @Bean public Queue queueProjectStatusAnalytics() { return new Queue(QUEUE_PROJECT_STATUS_ANALYTICS, true); }
    @Bean public Queue queueProjectStatusSearch() { return new Queue(QUEUE_PROJECT_STATUS_SEARCH, true); }
    @Bean public Queue queueProjectMemberAddedChat() { return new Queue(QUEUE_PROJECT_MEMBER_ADDED_CHAT, true); }
    @Bean public Queue queueProjectMemberAddedRating() { return new Queue(QUEUE_PROJECT_MEMBER_ADDED_RATING, true); }
    @Bean public Queue queueProjectMemberAddedPortfolio() { return new Queue(QUEUE_PROJECT_MEMBER_ADDED_PORTFOLIO, true); }
    @Bean public Queue queueProjectMemberRemovedChat() { return new Queue(QUEUE_PROJECT_MEMBER_REMOVED_CHAT, true); }
    @Bean public Queue queueProjectTaskCompletedRating() { return new Queue(QUEUE_PROJECT_TASK_COMPLETED_RATING, true); }
    @Bean public Queue queueProjectTaskCompletedAnalytics() { return new Queue(QUEUE_PROJECT_TASK_COMPLETED_ANALYTICS, true); }
    @Bean public Queue queueProjectDocumentUploadedSearch() { return new Queue(QUEUE_PROJECT_DOCUMENT_UPLOADED_SEARCH, true); }
    @Bean public Queue queueEventCreatedSearch() { return new Queue(QUEUE_EVENT_CREATED_SEARCH, true); }
    @Bean public Queue queueEventCreatedNotification() { return new Queue(QUEUE_EVENT_CREATED_NOTIFICATION, true); }
    @Bean public Queue queueEventStatusNotification() { return new Queue(QUEUE_EVENT_STATUS_NOTIFICATION, true); }
    @Bean public Queue queueEventStatusSearch() { return new Queue(QUEUE_EVENT_STATUS_SEARCH, true); }
    @Bean public Queue queueEventApplicationNotification() { return new Queue(QUEUE_EVENT_APPLICATION_NOTIFICATION, true); }
    @Bean public Queue queueEventDecidedNotification() { return new Queue(QUEUE_EVENT_DECIDED_NOTIFICATION, true); }
    @Bean public Queue queueEventDecidedRating() { return new Queue(QUEUE_EVENT_DECIDED_RATING, true); }
    @Bean public Queue queueEventTeamNotification() { return new Queue(QUEUE_EVENT_TEAM_NOTIFICATION, true); }
    @Bean public Queue queueEventCompletedRating() { return new Queue(QUEUE_EVENT_COMPLETED_RATING, true); }
    @Bean public Queue queueEventCompletedPortfolio() { return new Queue(QUEUE_EVENT_COMPLETED_PORTFOLIO, true); }
    @Bean public Queue queueEventCompletedAnalytics() { return new Queue(QUEUE_EVENT_COMPLETED_ANALYTICS, true); }
    @Bean public Queue queueRatingRecalculatedCore() { return new Queue(QUEUE_RATING_RECALCULATED_CORE, true); }
    @Bean public Queue queueRatingRecalculatedPortfolio() { return new Queue(QUEUE_RATING_RECALCULATED_PORTFOLIO, true); }
    @Bean public Queue queueDocumentGeneratedNotification() { return new Queue(QUEUE_DOCUMENT_GENERATED_NOTIFICATION, true); }
    @Bean public Queue queuePartnerCaseSearch() { return new Queue(QUEUE_PARTNER_CASE_SEARCH, true); }
    @Bean public Queue queuePartnerVacancySearch() { return new Queue(QUEUE_PARTNER_VACANCY_SEARCH, true); }
    @Bean public Queue queuePortfolioAchievementRating() { return new Queue(QUEUE_PORTFOLIO_ACHIEVEMENT_RATING, true); }
    @Bean public Queue queuePortfolioAchievementSearch() { return new Queue(QUEUE_PORTFOLIO_ACHIEVEMENT_SEARCH, true); }

    // ==================== BINDINGS ====================

    // --- User ---
    @Bean public Binding bindUserCreatedPortfolio() { return BindingBuilder.bind(queueUserCreatedPortfolio()).to(userExchange()).with(USER_CREATED_KEY); }
    @Bean public Binding bindUserCreatedSearch() { return BindingBuilder.bind(queueUserCreatedSearch()).to(userExchange()).with(USER_CREATED_KEY); }
    @Bean public Binding bindUserCreatedRating() { return BindingBuilder.bind(queueUserCreatedRating()).to(userExchange()).with(USER_CREATED_KEY); }
    @Bean public Binding bindUserUpdatedSearch() { return BindingBuilder.bind(queueUserUpdatedSearch()).to(userExchange()).with(USER_UPDATED_KEY); }

    // --- Project ---
    @Bean public Binding bindProjectCreatedChat() { return BindingBuilder.bind(queueProjectCreatedChat()).to(projectExchange()).with(PROJECT_CREATED_KEY); }
    @Bean public Binding bindProjectCreatedRating() { return BindingBuilder.bind(queueProjectCreatedRating()).to(projectExchange()).with(PROJECT_CREATED_KEY); }
    @Bean public Binding bindProjectCreatedSearch() { return BindingBuilder.bind(queueProjectCreatedSearch()).to(projectExchange()).with(PROJECT_CREATED_KEY); }
    @Bean public Binding bindProjectStatusRating() { return BindingBuilder.bind(queueProjectStatusRating()).to(projectExchange()).with(PROJECT_STATUS_CHANGED_KEY); }
    @Bean public Binding bindProjectStatusAnalytics() { return BindingBuilder.bind(queueProjectStatusAnalytics()).to(projectExchange()).with(PROJECT_STATUS_CHANGED_KEY); }
    @Bean public Binding bindProjectStatusSearch() { return BindingBuilder.bind(queueProjectStatusSearch()).to(projectExchange()).with(PROJECT_STATUS_CHANGED_KEY); }
    @Bean public Binding bindProjectMemberAddedChat() { return BindingBuilder.bind(queueProjectMemberAddedChat()).to(projectExchange()).with(PROJECT_MEMBER_ADDED_KEY); }
    @Bean public Binding bindProjectMemberAddedRating() { return BindingBuilder.bind(queueProjectMemberAddedRating()).to(projectExchange()).with(PROJECT_MEMBER_ADDED_KEY); }
    @Bean public Binding bindProjectMemberAddedPortfolio() { return BindingBuilder.bind(queueProjectMemberAddedPortfolio()).to(projectExchange()).with(PROJECT_MEMBER_ADDED_KEY); }
    @Bean public Binding bindProjectMemberRemovedChat() { return BindingBuilder.bind(queueProjectMemberRemovedChat()).to(projectExchange()).with(PROJECT_MEMBER_REMOVED_KEY); }
    @Bean public Binding bindProjectTaskCompletedRating() { return BindingBuilder.bind(queueProjectTaskCompletedRating()).to(projectExchange()).with(PROJECT_TASK_COMPLETED_KEY); }
    @Bean public Binding bindProjectTaskCompletedAnalytics() { return BindingBuilder.bind(queueProjectTaskCompletedAnalytics()).to(projectExchange()).with(PROJECT_TASK_COMPLETED_KEY); }
    @Bean public Binding bindProjectDocumentUploadedSearch() { return BindingBuilder.bind(queueProjectDocumentUploadedSearch()).to(projectExchange()).with(PROJECT_DOCUMENT_UPLOADED_KEY); }

    // --- Event ---
    @Bean public Binding bindEventCreatedSearch() { return BindingBuilder.bind(queueEventCreatedSearch()).to(eventExchange()).with(EVENT_CREATED_KEY); }
    @Bean public Binding bindEventCreatedNotification() { return BindingBuilder.bind(queueEventCreatedNotification()).to(eventExchange()).with(EVENT_CREATED_KEY); }
    @Bean public Binding bindEventStatusNotification() { return BindingBuilder.bind(queueEventStatusNotification()).to(eventExchange()).with(EVENT_STATUS_CHANGED_KEY); }
    @Bean public Binding bindEventStatusSearch() { return BindingBuilder.bind(queueEventStatusSearch()).to(eventExchange()).with(EVENT_STATUS_CHANGED_KEY); }
    @Bean public Binding bindEventApplicationNotification() { return BindingBuilder.bind(queueEventApplicationNotification()).to(eventExchange()).with(EVENT_APPLICATION_SUBMITTED_KEY); }
    @Bean public Binding bindEventDecidedNotification() { return BindingBuilder.bind(queueEventDecidedNotification()).to(eventExchange()).with(EVENT_APPLICATION_DECIDED_KEY); }
    @Bean public Binding bindEventDecidedRating() { return BindingBuilder.bind(queueEventDecidedRating()).to(eventExchange()).with(EVENT_APPLICATION_DECIDED_KEY); }
    @Bean public Binding bindEventTeamNotification() { return BindingBuilder.bind(queueEventTeamNotification()).to(eventExchange()).with(EVENT_TEAM_FORMED_KEY); }
    @Bean public Binding bindEventCompletedRating() { return BindingBuilder.bind(queueEventCompletedRating()).to(eventExchange()).with(EVENT_COMPLETED_KEY); }
    @Bean public Binding bindEventCompletedPortfolio() { return BindingBuilder.bind(queueEventCompletedPortfolio()).to(eventExchange()).with(EVENT_COMPLETED_KEY); }
    @Bean public Binding bindEventCompletedAnalytics() { return BindingBuilder.bind(queueEventCompletedAnalytics()).to(eventExchange()).with(EVENT_COMPLETED_KEY); }

    // --- Rating ---
    @Bean public Binding bindRatingRecalculatedCore() { return BindingBuilder.bind(queueRatingRecalculatedCore()).to(ratingExchange()).with(RATING_RECALCULATED_KEY); }
    @Bean public Binding bindRatingRecalculatedPortfolio() { return BindingBuilder.bind(queueRatingRecalculatedPortfolio()).to(ratingExchange()).with(RATING_RECALCULATED_KEY); }

    // --- Notification ---
    @Bean public Binding bindNotificationSend() { return BindingBuilder.bind(queueNotificationSend()).to(notificationExchange()).with(NOTIFICATION_SEND_KEY); }

    // --- Document ---
    @Bean public Binding bindDocumentGeneratedNotification() { return BindingBuilder.bind(queueDocumentGeneratedNotification()).to(documentExchange()).with(DOCUMENT_GENERATED_KEY); }

    // --- Partner ---
    @Bean public Binding bindPartnerCaseSearch() { return BindingBuilder.bind(queuePartnerCaseSearch()).to(partnerExchange()).with(PARTNER_CASE_PUBLISHED_KEY); }
    @Bean public Binding bindPartnerVacancySearch() { return BindingBuilder.bind(queuePartnerVacancySearch()).to(partnerExchange()).with(PARTNER_VACANCY_PUBLISHED_KEY); }

    // --- Portfolio ---
    @Bean public Binding bindPortfolioAchievementRating() { return BindingBuilder.bind(queuePortfolioAchievementRating()).to(portfolioExchange()).with(PORTFOLIO_ACHIEVEMENT_ADDED_KEY); }
    @Bean public Binding bindPortfolioAchievementSearch() { return BindingBuilder.bind(queuePortfolioAchievementSearch()).to(portfolioExchange()).with(PORTFOLIO_ACHIEVEMENT_ADDED_KEY); }
}
