package com.example.sharedlib.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Data Transfer Object for Role information.
 * Represents a user role within the system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the role.
     */
    private Long id;

    /**
     * The name of the role (e.g., "ROLE_STUDENT", "ROLE_ADMIN").
     */
    private String name;

    /**
     * A brief description of the role's permissions and responsibilities.
     */
    private String description;
}