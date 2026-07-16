package com.example.coreservice.listener;

import com.example.coreservice.dto.ChatDto;
import com.example.coreservice.service.ChatService;
import com.example.sharedlib.config.RabbitMqAutoConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectEventListener {

    private final ChatService chatService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMqAutoConfiguration.QUEUE_PROJECT_CREATED_CHAT)
    public void onProjectCreated(Map<String, Object> event) {
        log.info("Creating chat for new project: {}", event);
        String projectId = String.valueOf(event.get("projectId"));
        String title = (String) event.getOrDefault("title", "Project Chat");

        ChatDto chatDto = ChatDto.builder()
                .name(title + " - Chat")
                .build();
        chatService.createChat(chatDto);
        log.info("Chat created for project: {}", projectId);
    }

    @RabbitListener(queues = RabbitMqAutoConfiguration.QUEUE_PROJECT_MEMBER_ADDED_CHAT)
    public void onMemberAdded(Map<String, Object> event) {
        log.info("Member added to project, sending notification: {}", event);
        String userId = String.valueOf(event.get("userId"));
        String projectTitle = (String) event.getOrDefault("title", "проект");

        rabbitTemplate.convertAndSend(
                RabbitMqAutoConfiguration.NOTIFICATION_EXCHANGE,
                RabbitMqAutoConfiguration.NOTIFICATION_SEND_KEY,
                Map.of(
                        "userId", userId,
                        "title", "Добавление в проект",
                        "message", "Вы были добавлены в проект \"" + projectTitle + "\"",
                        "type", "PROJECT_INVITE"
                )
        );
    }

    @RabbitListener(queues = RabbitMqAutoConfiguration.QUEUE_PROJECT_MEMBER_REMOVED_CHAT)
    public void onMemberRemoved(Map<String, Object> event) {
        log.info("Member removed from project, sending notification: {}", event);
        String userId = String.valueOf(event.get("userId"));
        String projectTitle = (String) event.getOrDefault("title", "проект");

        rabbitTemplate.convertAndSend(
                RabbitMqAutoConfiguration.NOTIFICATION_EXCHANGE,
                RabbitMqAutoConfiguration.NOTIFICATION_SEND_KEY,
                Map.of(
                        "userId", userId,
                        "title", "Исключение из проекта",
                        "message", "Вы были исключены из проекта \"" + projectTitle + "\"",
                        "type", "SYSTEM"
                )
        );
    }
}
