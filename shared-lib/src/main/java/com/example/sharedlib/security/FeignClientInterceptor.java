package com.example.sharedlib.security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Feign interceptor that propagates X-User-Id and X-User-Role headers
 * to downstream microservice calls, preserving the security context.
 */
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            template.header(RoleHeaderFilter.USER_ID_HEADER, authentication.getPrincipal().toString());

            authentication.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                    .ifPresent(role -> template.header(RoleHeaderFilter.USER_ROLE_HEADER, role));
        }
    }
}