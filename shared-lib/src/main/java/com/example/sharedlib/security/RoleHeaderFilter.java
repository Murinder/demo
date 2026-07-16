package com.example.sharedlib.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Reads X-User-Id and X-User-Role headers (set by API Gateway after JWT validation)
 * and creates a Spring Security Authentication with proper GrantedAuthority.
 * This enables @PreAuthorize annotations in every microservice.
 */
@Slf4j
public class RoleHeaderFilter extends OncePerRequestFilter {

    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USER_ROLE_HEADER = "X-User-Role";
    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String userId = request.getHeader(USER_ID_HEADER);
            String userRole = request.getHeader(USER_ROLE_HEADER);

            if (StringUtils.hasText(userId) && StringUtils.hasText(userRole)) {
                String normalizedRole = userRole.trim().toUpperCase();

                List<GrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority(ROLE_PREFIX + normalizedRole)
                );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Set authentication for userId={}, role={}", userId, normalizedRole);
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            log.warn("Failed to set authentication from headers", e);
        }

        filterChain.doFilter(request, response);
    }
}