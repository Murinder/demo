package com.example.coreservice.listener;

import com.example.coreservice.search.SearchableDocument;
import com.example.coreservice.service.SearchService;
import com.example.sharedlib.config.RabbitMqAutoConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchEventListener {

    private final SearchService searchService;

    @RabbitListener(queues = RabbitMqAutoConfiguration.QUEUE_PROJECT_CREATED_SEARCH)
    public void onProjectCreated(Map<String, Object> event) {
        log.info("Indexing new project: {}", event);
        searchService.indexDocument(SearchableDocument.builder()
                .id("project-" + event.get("projectId"))
                .entityType("PROJECT")
                .title((String) event.get("title"))
                .description((String) event.get("description"))
                .createdBy(String.valueOf(event.get("createdBy")))
                .build());
    }

    @RabbitListener(queues = RabbitMqAutoConfiguration.QUEUE_EVENT_CREATED_SEARCH)
    public void onEventCreated(Map<String, Object> event) {
        log.info("Indexing new event: {}", event);
        searchService.indexDocument(SearchableDocument.builder()
                .id("event-" + event.get("eventId"))
                .entityType("EVENT")
                .title((String) event.get("title"))
                .description((String) event.get("description"))
                .build());
    }

    @RabbitListener(queues = RabbitMqAutoConfiguration.QUEUE_USER_CREATED_SEARCH)
    public void onUserCreated(Map<String, Object> event) {
        log.info("Indexing new user: {}", event);
        searchService.indexDocument(SearchableDocument.builder()
                .id("user-" + event.get("userId"))
                .entityType("USER")
                .title((String) event.get("fullName"))
                .description((String) event.get("email"))
                .build());
    }

    @RabbitListener(queues = RabbitMqAutoConfiguration.QUEUE_PROJECT_DOCUMENT_UPLOADED_SEARCH)
    public void onDocumentUploaded(Map<String, Object> event) {
        log.info("Indexing new document: {}", event);
        searchService.indexDocument(SearchableDocument.builder()
                .id("document-" + event.get("documentId"))
                .entityType("DOCUMENT")
                .title((String) event.get("title"))
                .build());
    }
}
