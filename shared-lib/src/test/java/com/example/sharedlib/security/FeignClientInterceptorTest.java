package com.example.sharedlib.security;

import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FeignClientInterceptorTest {

    private FeignClientInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new FeignClientInterceptor();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void apply_WithAuthentication_AddsUserIdAndRoleHeaders() {
        String userId = UUID.randomUUID().toString();
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userId, null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        RequestTemplate template = new RequestTemplate();
        interceptor.apply(template);

        assertThat(template.headers().get("X-User-Id")).contains(userId);
        assertThat(template.headers().get("X-User-Role")).contains("STUDENT");
    }

    @Test
    void apply_WithAdminRole_SetsAdminRoleHeader() {
        String userId = UUID.randomUUID().toString();
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userId, null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        RequestTemplate template = new RequestTemplate();
        interceptor.apply(template);

        assertThat(template.headers().get("X-User-Id")).contains(userId);
        assertThat(template.headers().get("X-User-Role")).contains("ADMIN");
    }

    @Test
    void apply_WithoutAuthentication_NoHeaders() {
        RequestTemplate template = new RequestTemplate();
        interceptor.apply(template);

        assertThat(template.headers()).doesNotContainKey("X-User-Id");
        assertThat(template.headers()).doesNotContainKey("X-User-Role");
    }

    @Test
    void apply_WithNullPrincipal_NoHeaders() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                null, null, Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        RequestTemplate template = new RequestTemplate();
        interceptor.apply(template);

        assertThat(template.headers()).doesNotContainKey("X-User-Id");
    }
}
