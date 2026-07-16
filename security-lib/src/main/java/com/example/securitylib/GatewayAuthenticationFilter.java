package com.example.securitylib;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
public class GatewayAuthenticationFilter extends OncePerRequestFilter {
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";
    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String userId = request.getHeader(USER_ID_HEADER);
        String userRole = request.getHeader(USER_ROLE_HEADER);

        try {
            if (StringUtils.hasText(userId) && StringUtils.hasText(userRole)) {
                // Нормализуем роль: убираем пробелы, приводим к верхнему регистру
                String normalizedRole = userRole.trim().toUpperCase();

                // Создаём authorities с правильным префиксом
                List<GrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority(ROLE_PREFIX + normalizedRole)
                );

                // Создаём аутентификацию
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        authorities
                );

                // Устанавливаем в контекст безопасности
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Очищаем контекст при ошибке обработки
            SecurityContextHolder.clearContext();
            // Можно добавить логирование ошибки
             log.warn("Failed to set authentication from headers", e);
        }

        // Продолжаем обработку запроса в любом случае
        filterChain.doFilter(request, response);
    }
}