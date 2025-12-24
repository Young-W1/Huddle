package com.capstone.huddle.users.service;

import com.capstone.huddle.notifications.service.EmailService;
import com.capstone.huddle.users.dto.request.PasswordResetConfirmRequest;
import com.capstone.huddle.users.dto.request.PasswordResetRequest;
import com.capstone.huddle.users.model.PasswordResetToken;
import com.capstone.huddle.users.model.UserEntity;
import com.capstone.huddle.users.repository.PasswordResetTokenRepository;
import com.capstone.huddle.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.password-reset.token-expiry-hours:24}")
    private int tokenExpiryHours;

    @Value("${app.base-url:http://localhost:3000}")
    private String baseUrl;

    @Value("${app.dev-mode:true}")
    private boolean devMode;

    @Transactional
    public String initiatePasswordReset(PasswordResetRequest request) {
        // Find user by email
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        // Always return success message to prevent email enumeration
        if (user == null) {
            log.warn("Password reset requested for non-existent email: {}", request.getEmail());
            return "If an account with that email exists, a password reset link has been sent.";
        }

        // Invalidate any existing tokens for this user
        tokenRepository.invalidateAllUserTokens(user);

        // Generate new token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(tokenExpiryHours))
                .used(false)
                .build();

        tokenRepository.save(resetToken);

        // Generate reset URL
        String resetUrl = baseUrl + "/reset-password?token=" + token;
        log.info("Password reset token generated for user: {}", user.getUsername());

        // Send password reset email (in dev mode, it will just log)
        emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), resetUrl);

        // In dev mode, return the URL directly for testing
        if (devMode) {
            return "Password reset link (DEV MODE): " + resetUrl;
        }

        return "If an account with that email exists, a password reset link has been sent to your email address.";
    }

    @Transactional
    public void resetPassword(PasswordResetConfirmRequest request) {
        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Find and validate token
        PasswordResetToken resetToken = tokenRepository.findByTokenAndUsedFalse(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        if (resetToken.isExpired()) {
            throw new RuntimeException("Reset token has expired");
        }

        // Update password
        UserEntity user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("Password successfully reset for user: {}", user.getUsername());
    }

    public boolean validateToken(String token) {
        return tokenRepository.findByTokenAndUsedFalse(token)
                .map(t -> !t.isExpired())
                .orElse(false);
    }

    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("Cleaned up expired password reset tokens");
    }
}

