package com.example.sharedlib.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A generic wrapper for all API responses.
 * This class provides a consistent structure for responses, including success status,
 * a message, an error code, and the actual data payload.
 *
 * @param <T> The type of the data payload.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Indicates whether the operation was successful.
     */
    private boolean success;

    /**
     * An optional code representing the result of the operation (e.g., "SUCCESS", "VALIDATION_ERROR").
     */
    private String code;

    /**
     * A message providing more details about the result of the operation.
     */
    private String message;

    /**
     * The actual data payload of the response.
     */
    private T data;

    /**
     * The timestamp when the response was generated.
     */
    private LocalDateTime timestamp;

    /**
     * Creates a successful API response with the given data.
     *
     * @param data The data payload.
     * @param <T>  The type of the data.
     * @return An ApiResponse instance representing a successful operation.
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code("SUCCESS")
                .message("Operation completed successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a successful API response with the given data and a custom message.
     *
     * @param data    The data payload.
     * @param message A custom success message.
     * @param <T>     The type of the data.
     * @return An ApiResponse instance representing a successful operation.
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .code("SUCCESS")
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates an error API response with the given error code and message.
     *
     * @param code    The error code.
     * @param message The error message.
     * @param <T>     The type of the data (usually null for errors).
     * @return An ApiResponse instance representing a failed operation.
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}