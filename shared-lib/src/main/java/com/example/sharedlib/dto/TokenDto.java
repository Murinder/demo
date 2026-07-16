package com.example.sharedlib.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Data Transfer Object for JWT (JSON Web Token) data.
 * Used to send authentication tokens to the client after a successful login.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The JWT access token used for authenticating requests.
     */
    private String accessToken;

    /**
     * The refresh token used to obtain a new access token without re-authenticating.
     */
    private String refreshToken;

    /**
     * The expiration time of the access token in seconds.
     */
    private long expiresIn;

    /**
     * The type of the token (e.g., "Bearer").
     */
    private String tokenType;
}