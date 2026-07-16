package com.example.coreservice.controller;

import com.example.coreservice.integration.SsoAuthenticationProvider;
import com.example.sharedlib.dto.UserProfileDto;
import com.example.sharedlib.response.ApiResponse;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth/sso")
@RequiredArgsConstructor
@Tag(name = "SSO Authentication", description = "SSO/LDAP authentication endpoints")
public class SsoController {

    private final SsoAuthenticationProvider ssoAuthenticationProvider;

    @PostMapping("/callback")
    @Operation(summary = "SSO callback", description = "Processes SSO SAML response and authenticates user")
    public ResponseEntity<ApiResponse<UserProfileDto>> ssoCallback(@RequestBody Map<String, String> payload) {
        log.info("SSO callback received");
        String samlResponse = payload.getOrDefault("samlResponse", "");
        UserProfileDto user = ssoAuthenticationProvider.authenticate(samlResponse);
        return ResponseEntity.ok(ApiResponse.<UserProfileDto>builder()
                .success(true)
                .code("SSO_AUTH_SUCCESS")
                .message("SSO authentication successful")
                .data(user)
                .build());
    }
}
