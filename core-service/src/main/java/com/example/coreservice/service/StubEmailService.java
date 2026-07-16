package com.example.coreservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StubEmailService implements EmailService {

    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        log.info("[STUB EMAIL] Password reset email to: {}, token: {}", to, resetToken);
    }

    @Override
    public void sendVerificationEmail(String to, String verificationToken) {
        log.info("[STUB EMAIL] Verification email to: {}, token: {}", to, verificationToken);
    }

    @Override
    public void sendTransactional(String to, String subject, String body) {
        log.info("[STUB EMAIL] Transactional email to: {}, subject: {}", to, subject);
    }
}
