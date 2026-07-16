package com.example.portfolioservice.listener;

import com.example.portfolioservice.service.PortfolioService;
import com.example.sharedlib.config.RabbitMqAutoConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PortfolioEventListener {

    private final PortfolioService portfolioService;

    @RabbitListener(queues = RabbitMqAutoConfiguration.QUEUE_USER_CREATED_PORTFOLIO)
    public void onUserCreated(Map<String, Object> event) {
        log.info("User created event received, creating portfolio: {}", event);
        String userIdStr = String.valueOf(event.get("userId"));
        portfolioService.createPortfolio(UUID.fromString(userIdStr));
    }

    @RabbitListener(queues = RabbitMqAutoConfiguration.QUEUE_EVENT_COMPLETED_PORTFOLIO)
    public void onEventCompleted(Map<String, Object> event) {
        log.info("Event completed, may add achievement: {}", event);
        // Auto-add achievement when an event completes - requires event details
    }

    @RabbitListener(queues = RabbitMqAutoConfiguration.QUEUE_RATING_RECALCULATED_PORTFOLIO)
    public void onRatingRecalculated(Map<String, Object> event) {
        log.info("Rating recalculated for portfolio update: {}", event);
        // Update portfolio with new rating data
    }
}
