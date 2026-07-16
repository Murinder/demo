package com.example.testsupport.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * Utility for adding security headers to MockMvc requests.
 * Simulates requests that have already passed through the API Gateway's
 * AuthorizationHeaderFilter, which sets X-User-Id and X-User-Role headers.
 */
public final class TestSecurityHelper {

    public static final String TEST_JWT_SECRET = "test-secret-key-for-testing-only-minimum-32-characters-long";
    private static final long JWT_EXPIRATION = 3600000L; // 1 hour

    private TestSecurityHelper() {}

    /**
     * Add authentication headers matching what RoleHeaderFilter expects.
     */
    public static MockHttpServletRequestBuilder withUser(MockHttpServletRequestBuilder builder,
                                                          UUID userId, String role) {
        return builder
                .header("X-User-Id", userId.toString())
                .header("X-User-Role", role);
    }

    public static MockHttpServletRequestBuilder withStudent(MockHttpServletRequestBuilder builder, UUID userId) {
        return withUser(builder, userId, "STUDENT");
    }

    public static MockHttpServletRequestBuilder withLecturer(MockHttpServletRequestBuilder builder, UUID userId) {
        return withUser(builder, userId, "LECTURER");
    }

    public static MockHttpServletRequestBuilder withAdmin(MockHttpServletRequestBuilder builder, UUID userId) {
        return withUser(builder, userId, "ADMIN");
    }

    public static MockHttpServletRequestBuilder withPartner(MockHttpServletRequestBuilder builder, UUID userId) {
        return withUser(builder, userId, "PARTNER");
    }

    public static MockHttpServletRequestBuilder withDepartmentHead(MockHttpServletRequestBuilder builder, UUID userId) {
        return withUser(builder, userId, "DEPARTMENT_HEAD");
    }

    /**
     * Generate a real JWT token for gateway-level tests.
     */
    public static String generateTestJwt(UUID userId, String role) {
        SecretKey key = Keys.hmacShaKeyFor(TEST_JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(userId.toString())
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(key)
                .compact();
    }

    /**
     * Generate an expired JWT for testing token expiration.
     */
    public static String generateExpiredJwt(UUID userId, String role) {
        SecretKey key = Keys.hmacShaKeyFor(TEST_JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(userId.toString())
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis() - 7200000L))
                .expiration(new Date(System.currentTimeMillis() - 3600000L))
                .signWith(key)
                .compact();
    }
}
