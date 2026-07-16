package com.example.coreservice.service;

public interface EmailService {
    void sendPasswordResetEmail(String to, String resetToken);
    void sendVerificationEmail(String to, String verificationToken);
    void sendTransactional(String to, String subject, String body);
}
