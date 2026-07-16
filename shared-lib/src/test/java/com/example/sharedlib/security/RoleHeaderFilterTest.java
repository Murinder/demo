package com.example.sharedlib.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleHeaderFilterTest {

    private RoleHeaderFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new RoleHeaderFilter();
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_WithValidHeaders_SetsSecurityContext() throws Exception {
        String userId = "550e8400-e29b-41d4-a716-446655440000";
        when(request.getHeader("X-User-Id")).thenReturn(userId);
        when(request.getHeader("X-User-Role")).thenReturn("STUDENT");

        filter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isEqualTo(userId);
        assertThat(auth.getAuthorities()).hasSize(1);
        assertThat(auth.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_STUDENT");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_WithAdminRole_SetsRoleAdmin() throws Exception {
        when(request.getHeader("X-User-Id")).thenReturn("some-id");
        when(request.getHeader("X-User-Role")).thenReturn("admin");

        filter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void doFilter_WithoutHeaders_NoAuthentication() throws Exception {
        when(request.getHeader("X-User-Id")).thenReturn(null);
        when(request.getHeader("X-User-Role")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_WithEmptyHeaders_NoAuthentication() throws Exception {
        when(request.getHeader("X-User-Id")).thenReturn("");
        when(request.getHeader("X-User-Role")).thenReturn("");

        filter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
    }

    @Test
    void doFilter_WithUserIdOnly_NoAuthentication() throws Exception {
        when(request.getHeader("X-User-Id")).thenReturn("some-id");
        when(request.getHeader("X-User-Role")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
    }

    @Test
    void doFilter_NormalizesRoleToUppercase() throws Exception {
        when(request.getHeader("X-User-Id")).thenReturn("some-id");
        when(request.getHeader("X-User-Role")).thenReturn("  lecturer  ");

        filter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_LECTURER");
    }

    @Test
    void doFilter_AlwaysCallsFilterChain() throws Exception {
        when(request.getHeader("X-User-Id")).thenReturn(null);
        when(request.getHeader("X-User-Role")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }
}
