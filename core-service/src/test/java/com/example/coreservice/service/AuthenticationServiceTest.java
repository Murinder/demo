package com.example.coreservice.service;

import com.example.coreservice.dto.RegisterRequest;
import com.example.coreservice.model.entity.User;
import com.example.sharedlib.security.JwtTokenProvider;
import com.example.sharedlib.dto.TokenDto;
import com.example.sharedlib.dto.UserProfileDto;
import com.example.sharedlib.enums.UserRole;
import com.example.sharedlib.exception.AuthenticationException;
import com.example.coreservice.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.sharedlib.event.EventPublisher;
import com.example.sharedlib.event.UserEvent;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("Password1");
        registerRequest.setPhoneNumber("1234567890");

        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .passwordHash("encodedPassword")
                .role(UserRole.STUDENT)
                .isActive(true)
                .build();
    }

    @Test
    void register_SendsRabbitMqMessage() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(UUID.randomUUID());
            return user;
        });

        UserProfileDto result = authenticationService.register(registerRequest);

        assertNotNull(result);
        assertEquals(registerRequest.getEmail(), result.getEmail());
        verify(eventPublisher, times(1)).publish(eq("etsopy.user"), eq("user.created"), any(UserEvent.class));
    }

    @Test
    void refreshToken_WithValidToken_ReturnsNewTokens() {
        String refreshToken = "valid-refresh-token";
        Claims claims = mock(Claims.class);
        when(claims.get("type", String.class)).thenReturn("refresh");
        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getClaimsFromToken(refreshToken)).thenReturn(claims);
        when(jwtTokenProvider.getUserIdFromToken(refreshToken)).thenReturn(testUser.getId().toString());
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateAccessToken(any(), any(), any())).thenReturn("new-access-token");
        when(jwtTokenProvider.generateRefreshToken(any())).thenReturn("new-refresh-token");

        TokenDto result = authenticationService.refreshToken(refreshToken);

        assertNotNull(result);
        assertEquals("new-access-token", result.getAccessToken());
        assertEquals("new-refresh-token", result.getRefreshToken());
    }

    @Test
    void refreshToken_WithInvalidToken_ThrowsException() {
        when(jwtTokenProvider.validateToken("invalid-token")).thenReturn(false);

        assertThrows(AuthenticationException.class,
                () -> authenticationService.refreshToken("invalid-token"));
    }

    @Test
    void forgotPassword_WithValidEmail_SendsEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateAccessToken(any(), any(), eq("RESET"))).thenReturn("reset-token");

        authenticationService.forgotPassword("test@example.com");

        verify(emailService, times(1)).sendPasswordResetEmail("test@example.com", "reset-token");
    }

    @Test
    void resetPassword_WithValidToken_UpdatesPassword() {
        String resetToken = "valid-reset-token";
        when(jwtTokenProvider.validateToken(resetToken)).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(resetToken)).thenReturn(testUser.getId().toString());
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("NewPassword1")).thenReturn("newEncodedPassword");

        authenticationService.resetPassword(resetToken, "NewPassword1");

        verify(userRepository, times(1)).save(testUser);
        assertEquals("newEncodedPassword", testUser.getPasswordHash());
    }
}
