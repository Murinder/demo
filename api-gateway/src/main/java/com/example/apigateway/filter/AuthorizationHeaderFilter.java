package com.example.apigateway.filter;

import com.example.securitylib.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config>
implements Ordered {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    private final JwtTokenProvider tokenProvider;

    public AuthorizationHeaderFilter(JwtTokenProvider tokenProvider) {
        super(Config.class);
        this.tokenProvider = tokenProvider;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.info("🚀 Processing request to path: {}", exchange.getRequest().getURI().getPath());
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            if (isPublicPath(path)) {
                return chain.filter(exchange);
            }

            String token = extractTokenFromRequest(request);
            if (!StringUtils.hasText(token) || !tokenProvider.validateToken(token)) {
                log.warn("Authorization token is missing or invalid for path: {}", path);
                return onError(exchange, "Authorization token is missing or invalid", HttpStatus.UNAUTHORIZED);
            }

            try {
                Claims claims = tokenProvider.getClaimsFromToken(token);
                String userId = claims.getSubject();
                String role = claims.get("role", String.class);

                ServerHttpRequest modifiedRequest = request.mutate()
                        .header(USER_ID_HEADER, userId)
                        .header(USER_ROLE_HEADER, role)
                        .headers(headers -> headers.remove(HttpHeaders.AUTHORIZATION))
                        .build();
                log.info("✅ Added headers: X-User-Id={}, X-User-Role={}", userId, role);
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (Exception e) {
                log.error("Error while processing token", e);
                return onError(exchange, "Error while processing token", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    public static class Config {
        // Put the configuration properties here
    }

    private boolean isPublicPath(String path) {
        return path.equals("/api/v1/auth/login") ||
                path.equals("/api/v1/auth/register") ||
                path.startsWith("/actuator") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/service-docs");
    }

    private String extractTokenFromRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -100; // Критически высокий приоритет
    }
}