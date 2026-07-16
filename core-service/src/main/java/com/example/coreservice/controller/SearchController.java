package com.example.coreservice.controller;

import com.example.coreservice.search.SearchableDocument;
import com.example.coreservice.service.SearchService;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Search", description = "Global search APIs")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    @Operation(summary = "Search across all entities", description = "Search projects, events, users, documents")
    public ResponseEntity<List<SearchableDocument>> search(
            @RequestParam String q,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(searchService.search(q, type, page, size));
    }
}
