package com.example.sharedlib.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Data Transfer Object for User information.
 * Used to transfer user data between different layers of the application,
 * particularly between the server and clients.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the user.
     */
    private Long id;

    /**
     * The user's email address. Must be unique.
     */
    private String email;

    /**
     * The user's first name.
     */
    private String firstName;

    /**
     * The user's last name.
     */
    private String lastName;

    /**
     * The role of the user (e.g., "STUDENT", "LECTURER", "ADMIN").
     */
    private String role;

    /**
     * Indicates whether the user's account is active.
     */
    private boolean active;

    /**
     * The URL of the user's avatar image.
     */
    private String avatar;

    /**
     * The user's phone number.
     */
    private String phone;
}