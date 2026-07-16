package com.example.coreservice.config;

import com.example.coreservice.model.entity.User;
import com.example.coreservice.repository.UserRepository;
import com.example.sharedlib.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class DemoDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createIfNotExists("student@university.edu", "student123", "Иван", "Студентов", UserRole.STUDENT);
        createIfNotExists("teacher@university.edu", "teacher123", "Мария", "Преподавателева", UserRole.LECTURER);
        createIfNotExists("head@university.edu", "head12345", "Алексей", "Заведующев", UserRole.DEPARTMENT_HEAD);
    }

    private void createIfNotExists(String email, String password, String firstName, String lastName, UserRole role) {
        if (userRepository.existsByEmail(email)) {
            log.debug("Demo user {} already exists", email);
            return;
        }

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .role(role)
                .isVerified(true)
                .isActive(true)
                .build();

        userRepository.save(user);
        log.info("Created demo user: {} ({})", email, role);
    }
}
