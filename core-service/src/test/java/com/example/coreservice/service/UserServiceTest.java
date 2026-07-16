package com.example.coreservice.service;

import com.example.coreservice.dto.UserDto;
import com.example.coreservice.dto.UserRegistrationDto;
import com.example.coreservice.model.entity.User;
import com.example.sharedlib.dto.UserProfileDto;
import com.example.sharedlib.enums.UserRole;
import com.example.coreservice.repository.UserRepository;
import com.example.sharedlib.exception.ValidationException;
import com.example.sharedlib.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRegistrationDto registrationDto;

    @BeforeEach
    void setUp() {
        registrationDto = new UserRegistrationDto();
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("password");
    }

    @Test
    void registerUser_Success() {
        when(userRepository.existsByEmail(registrationDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registrationDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(UUID.randomUUID());
            return user;
        });

        UserDto result = userService.registerUser(registrationDto);

        assertNotNull(result);
        assertEquals(registrationDto.getEmail(), result.getEmail());
        assertEquals(UserRole.STUDENT, result.getRole());
        assertFalse(result.getIsVerified());
        assertTrue(result.getIsActive());
    }

    @Test
    void registerUser_EmailAlreadyExists() {
        when(userRepository.existsByEmail(registrationDto.getEmail())).thenReturn(true);

        assertThrows(ValidationException.class, () -> {
            userService.registerUser(registrationDto);
        });
    }

    @Test
    void getUserById_Success() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setRole(UserRole.STUDENT);
        user.setIsActive(true);

        when(userRepository.findByIdAndIsActiveTrue(userId)).thenReturn(Optional.of(user));

        UserProfileDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getUserById_NotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findByIdAndIsActiveTrue(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(userId);
        });
    }

    @Test
    void updateUserProfile_Success() {
        UUID userId = UUID.randomUUID();
        UserProfileDto updateDto = new UserProfileDto();
        updateDto.setPhoneNumber("1234567890");
        updateDto.setAddress("123 Main St");

        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setRole(UserRole.STUDENT);
        user.setIsActive(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProfileDto result = userService.updateUserProfile(userId, updateDto);

        assertNotNull(result);
        assertEquals("1234567890", result.getPhoneNumber());
        assertEquals("123 Main St", result.getAddress());
    }

    @Test
    void updateUserProfile_NotFound() {
        UUID userId = UUID.randomUUID();
        UserProfileDto updateDto = new UserProfileDto();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUserProfile(userId, updateDto);
        });
    }
}