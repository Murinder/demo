package com.example.coreservice.controller;

import com.example.coreservice.dto.*;
import com.example.coreservice.service.AuthenticationService;
import com.example.coreservice.service.UserService;
import com.example.sharedlib.dto.TokenDto;
import com.example.sharedlib.dto.UserProfileDto;
import com.example.sharedlib.response.ApiResponse;
import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.sharedlib.security.AuthenticatedOnly;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints для аутентификации и управления учетными записями")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    /**
     * Регистрация нового пользователя
     */
    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя", description = "Создает новую учетную запись с корпоративной почтой")
    public ResponseEntity<ApiResponse<UserProfileDto>> register(@RequestBody RegisterRequest registrationDto) {
        log.info("Registration request for email: {}", registrationDto.getEmail());

        UserProfileDto userProfileDto = authenticationService.register(registrationDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<UserProfileDto>builder()
                        .success(true)
                        .code("USER_REGISTERED")
                        .message("User registered successfully")
                        .data(userProfileDto)
                        .build());
    }

    /**
     * Вход пользователя
     */
    @PostMapping("/login")
    @Operation(summary = "Вход пользователя", description = "Аутентифицирует пользователя и возвращает JWT токен")
    public ResponseEntity<ApiResponse<TokenDto>> login(@RequestBody LoginRequest loginDto) {
        log.info("Login request for email: {}", loginDto.getEmail());

        TokenDto tokenDto = authenticationService.login(loginDto);

        return ResponseEntity.ok(ApiResponse.<TokenDto>builder()
                .success(true)
                .code("LOGIN_SUCCESS")
                .message("User logged in successfully")
                .data(tokenDto)
                .build());
    }

    @AuthenticatedOnly
    @GetMapping("/profile/{userId}")
    @Operation(summary = "Получить профиль пользователя", description = "Возвращает информацию профиля пользователя")
    public ResponseEntity<ApiResponse<UserProfileDto>> getProfile(@PathVariable String userId) {
        log.info("Getting profile for user: {}", userId);

        UserProfileDto userProfileDto = authenticationService.getUserProfile(java.util.UUID.fromString(userId));

        return ResponseEntity.ok(ApiResponse.<UserProfileDto>builder()
                .success(true)
                .code("PROFILE_RETRIEVED")
                .message("User profile retrieved successfully")
                .data(userProfileDto)
                .build());
    }

    @AuthenticatedOnly
    @PutMapping("/profile/{userId}")
    @Operation(summary = "Обновить профиль пользователя", description = "Обновляет информацию профиля пользователя")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateProfile(
            @PathVariable String userId,
            @RequestBody UserProfileDto updateDto) {
        log.info("Updating profile for user: {}", userId);

        UserProfileDto updatedUser = userService.updateUserProfile(java.util.UUID.fromString(userId), updateDto);

        return ResponseEntity.ok(ApiResponse.<UserProfileDto>builder()
                .success(true)
                .code("PROFILE_UPDATED")
                .message("User profile updated successfully")
                .data(updatedUser)
                .build());
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Generates new access and refresh tokens using a valid refresh token")
    public ResponseEntity<ApiResponse<TokenDto>> refreshToken(@RequestBody RefreshTokenRequest request) {
        log.info("Token refresh request");
        TokenDto tokenDto = authenticationService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.<TokenDto>builder()
                .success(true)
                .code("TOKEN_REFRESHED")
                .message("Token refreshed successfully")
                .data(tokenDto)
                .build());
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset", description = "Sends a password reset email to the user")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        log.info("Password reset request for email: {}", request.getEmail());
        authenticationService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .code("RESET_EMAIL_SENT")
                .message("Password reset email sent successfully")
                .build());
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Resets user password using a valid reset token")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordRequest request) {
        log.info("Password reset attempt");
        authenticationService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .code("PASSWORD_RESET")
                .message("Password reset successfully")
                .build());
    }

    @AuthenticatedOnly
    @GetMapping("/profile/{userId}/teacher-details")
    @Operation(summary = "Получить расширенный профиль преподавателя", description = "Возвращает профиль с академическими метриками и наградами")
    public ResponseEntity<ApiResponse<TeacherProfileDto>> getTeacherProfileDetails(@PathVariable String userId) {
        log.info("Getting teacher profile details for user: {}", userId);

        TeacherProfileDto teacherProfile = userService.getTeacherProfile(UUID.fromString(userId));

        return ResponseEntity.ok(ApiResponse.<TeacherProfileDto>builder()
                .success(true)
                .code("TEACHER_PROFILE_RETRIEVED")
                .message("Teacher profile details retrieved successfully")
                .data(teacherProfile)
                .build());
    }

    @AuthenticatedOnly
    @GetMapping("/profile/{userId}/head-details")
    @Operation(summary = "Получить расширенный профиль заведующего", description = "Возвращает профиль с академическими метриками и статистикой кафедры")
    public ResponseEntity<ApiResponse<HeadProfileDto>> getHeadProfileDetails(@PathVariable String userId) {
        log.info("Getting head profile details for user: {}", userId);

        HeadProfileDto headProfile = userService.getHeadProfile(UUID.fromString(userId));

        return ResponseEntity.ok(ApiResponse.<HeadProfileDto>builder()
                .success(true)
                .code("HEAD_PROFILE_RETRIEVED")
                .message("Head profile details retrieved successfully")
                .data(headProfile)
                .build());
    }
}