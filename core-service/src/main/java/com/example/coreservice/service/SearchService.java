package com.example.coreservice.service;

import com.example.coreservice.search.SearchableDocument;
import com.example.coreservice.search.SearchableDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final SearchableDocumentRepository repository;

    public List<SearchableDocument> search(String query, String type, int page, int size) {
        Criteria criteria = new Criteria("title").contains(query)
                .or(new Criteria("description").contains(query));

        if (type != null && !type.isBlank()) {
            criteria = criteria.and(new Criteria("entityType").is(type));
        }

        Query searchQuery = new CriteriaQuery(criteria)
                .setPageable(PageRequest.of(page, size));

        SearchHits<SearchableDocument> hits = elasticsearchOperations.search(searchQuery, SearchableDocument.class);
        return hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    public void indexDocument(SearchableDocument document) {
        log.debug("Indexing document: {} ({})", document.getId(), document.getEntityType());
        repository.save(document);
    }

    public void deleteDocument(String id) {
        log.debug("Deleting document from index: {}", id);
        repository.deleteById(id);
    }
}
