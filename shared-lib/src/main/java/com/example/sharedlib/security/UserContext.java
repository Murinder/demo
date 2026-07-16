package com.example.sharedlib.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

/**
 * Utility class for accessing current user information from SecurityContext.
 * Works with the RoleHeaderFilter which sets userId as principal and role as authority.
 */
public final class UserContext {

    private UserContext() {
    }

    /**
     * Returns the current authenticated user's ID.
     */
    public static UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("No authenticated user in SecurityContext");
        }
        return UUID.fromString(auth.getPrincipal().toString());
    }

    /**
     * Returns the current user's ID as String, or null if not authenticated.
     */
    public static String getCurrentUserIdOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return null;
        }
        return auth.getPrincipal().toString();
    }

    /**
     * Returns the current user's role (without ROLE_ prefix).
     */
    public static String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null || auth.getAuthorities().isEmpty()) {
            throw new IllegalStateException("No role found in SecurityContext");
        }
        return auth.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                .orElseThrow(() -> new IllegalStateException("No role found"));
    }

    /**
     * Checks if the current user has a specific role.
     */
    public static boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        String expected = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(expected));
    }

    /**
     * Returns true if there is an authenticated user in context.
     */
    public static boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getPrincipal() != null
                && auth.getAuthorities() != null && !auth.getAuthorities().isEmpty();
    }
}