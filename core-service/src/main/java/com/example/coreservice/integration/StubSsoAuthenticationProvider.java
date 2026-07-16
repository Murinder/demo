package com.example.coreservice.integration;

import com.example.sharedlib.dto.UserProfileDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Stub implementation of SSO authentication provider.
 * Returns hardcoded test user data for development/testing.
 */
@Slf4j
@Service
public class StubSsoAuthenticationProvider implements SsoAuthenticationProvider {

    private static final UUID STUB_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Override
    public UserProfileDto authenticate(String samlResponse) {
        log.info("[STUB SSO] Authenticating SAML response (length={})", samlResponse != null ? samlResponse.length() : 0);

        return UserProfileDto.builder()
                .id(STUB_USER_ID)
                .email("sso.testuser@university.edu")
                .firstName("Test")
                .lastName("SsoUser")
                .role("STUDENT")
                .active(true)
                .createdAt(OffsetDateTime.now())
                .build();
    }

    @Override
    public Map<String, String> getUserAttributes(String token) {
        log.info("[STUB SSO] Getting user attributes for token: {}", token);

        Map<String, String> attributes = new HashMap<>();
        attributes.put("uid", STUB_USER_ID.toString());
        attributes.put("email", "sso.testuser@university.edu");
        attributes.put("displayName", "Test SsoUser");
        attributes.put("department", "Computer Science");
        attributes.put("role", "STUDENT");
        return attributes;
    }
}
