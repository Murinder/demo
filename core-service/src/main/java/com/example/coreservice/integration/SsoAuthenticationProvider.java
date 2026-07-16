package com.example.coreservice.integration;

import com.example.sharedlib.dto.UserProfileDto;

import java.util.Map;

/**
 * Interface for SSO/LDAP authentication integration.
 */
public interface SsoAuthenticationProvider {
    UserProfileDto authenticate(String samlResponse);
    Map<String, String> getUserAttributes(String token);
}
