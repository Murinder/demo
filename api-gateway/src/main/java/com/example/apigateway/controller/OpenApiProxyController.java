package com.example.apigateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/service-docs")
public class OpenApiProxyController {

    private final WebClient.Builder webClientBuilder;
    private final RouteDefinitionLocator locator;

    @Autowired
    public OpenApiProxyController(WebClient.Builder webClientBuilder, RouteDefinitionLocator locator) {
        this.webClientBuilder = webClientBuilder;
        this.locator = locator;
    }

    @GetMapping("/{service}")
    public Mono<ResponseEntity<String>> proxyOpenApi(@PathVariable String service) {
        String url = "http://" + service + "/v3/api-docs";
        return webClientBuilder.build().get()
                .uri(url)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(String.class)
                .map(body -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }
}