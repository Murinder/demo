package com.example.coreservice.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchableDocumentRepository extends ElasticsearchRepository<SearchableDocument, String> {
    List<SearchableDocument> findByEntityType(String entityType);
    List<SearchableDocument> findByTitleContainingOrDescriptionContaining(String title, String description);
}
