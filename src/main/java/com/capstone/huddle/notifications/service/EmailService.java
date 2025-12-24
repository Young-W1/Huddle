package com.capstone.huddle.notifications.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from-address:noreply@huddle.com}")
    private String fromAddress;

    @Value("${app.mail.from-name:Huddle}")
    private String fromName;

    @Value("${app.dev-mode:true}")
    private boolean devMode;

    @Async
    public void sendPasswordResetEmail(String toEmail, String username, String resetUrl) {
        if (devMode) {
            log.info("DEV MODE: Would send password reset email to {} with URL: {}", toEmail, resetUrl);
            return;
        }

        String subject = "Reset Your Huddle Password";
        String htmlContent = buildPasswordResetEmailContent(username, resetUrl);

        try {
            sendHtmlEmail(toEmail, subject, htmlContent);
            log.info("Password reset email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
        }
    }

    @Async
    public void sendWelcomeEmail(String toEmail, String username) {
        if (devMode) {
            log.info("DEV MODE: Would send welcome email to {}", toEmail);
            return;
        }

        String subject = "Welcome to Huddle!";
        String htmlContent = buildWelcomeEmailContent(username);

        try {
            sendHtmlEmail(toEmail, subject, htmlContent);
            log.info("Welcome email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", toEmail, e);
        }
    }

    public void sendHtmlEmail(String toEmail, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        try {
            helper.setFrom(fromAddress, fromName);
        } catch (java.io.UnsupportedEncodingException e) {
            helper.setFrom(fromAddress);
        }
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    private String buildPasswordResetEmailContent(String username, String resetUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html><head><meta charset=\"UTF-8\"></head>");
        sb.append("<body style=\"margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f5f5f5;\">");
        sb.append("<table style=\"width:100%;border-collapse:collapse;\"><tr><td align=\"center\" style=\"padding:40px 0;\">");
        sb.append("<table style=\"width:600px;background-color:#ffffff;border-radius:8px;\">");

        // Header
        sb.append("<tr><td style=\"padding:40px;text-align:center;background-color:#1976d2;border-radius:8px 8px 0 0;\">");
        sb.append("<h1 style=\"margin:0;color:#ffffff;font-size:28px;\">Huddle</h1>");
        sb.append("</td></tr>");

        // Content
        sb.append("<tr><td style=\"padding:40px;\">");
        sb.append("<h2 style=\"margin:0 0 20px 0;color:#333333;font-size:24px;\">Password Reset Request</h2>");
        sb.append("<p style=\"margin:0 0 20px 0;color:#666666;font-size:16px;line-height:1.6;\">");
        sb.append("Hi <strong>").append(username).append("</strong>,</p>");
        sb.append("<p style=\"margin:0 0 20px 0;color:#666666;font-size:16px;line-height:1.6;\">");
        sb.append("We received a request to reset your password. Click the button below to create a new password:</p>");

        // Button
        sb.append("<table style=\"width:100%;\"><tr><td align=\"center\" style=\"padding:20px 0;\">");
        sb.append("<a href=\"").append(resetUrl).append("\" style=\"display:inline-block;padding:14px 32px;background-color:#1976d2;color:#ffffff;text-decoration:none;border-radius:6px;font-size:16px;font-weight:600;\">");
        sb.append("Reset Password</a>");
        sb.append("</td></tr></table>");

        sb.append("<p style=\"margin:20px 0 0 0;color:#666666;font-size:14px;\">This link will expire in 24 hours.</p>");
        sb.append("<p style=\"margin:20px 0 0 0;color:#666666;font-size:14px;\">If you didn't request this, you can safely ignore this email.</p>");

        // Alternative link
        sb.append("<p style=\"margin:30px 0 0 0;color:#999999;font-size:12px;\">");
        sb.append("Or copy this link: <a href=\"").append(resetUrl).append("\" style=\"color:#1976d2;\">").append(resetUrl).append("</a></p>");
        sb.append("</td></tr>");

        // Footer
        sb.append("<tr><td style=\"padding:30px 40px;background-color:#f9f9f9;border-radius:0 0 8px 8px;text-align:center;\">");
        sb.append("<p style=\"margin:0;color:#999999;font-size:12px;\">2025 Huddle. All rights reserved.</p>");
        sb.append("</td></tr>");

        sb.append("</table></td></tr></table></body></html>");

        return sb.toString();
    }

    private String buildWelcomeEmailContent(String username) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html><head><meta charset=\"UTF-8\"></head>");
        sb.append("<body style=\"margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f5f5f5;\">");
        sb.append("<table style=\"width:100%;border-collapse:collapse;\"><tr><td align=\"center\" style=\"padding:40px 0;\">");
        sb.append("<table style=\"width:600px;background-color:#ffffff;border-radius:8px;\">");

        // Header
        sb.append("<tr><td style=\"padding:40px;text-align:center;background-color:#1976d2;border-radius:8px 8px 0 0;\">");
        sb.append("<h1 style=\"margin:0;color:#ffffff;font-size:28px;\">Welcome to Huddle!</h1>");
        sb.append("</td></tr>");

        // Content
        sb.append("<tr><td style=\"padding:40px;\">");
        sb.append("<h2 style=\"margin:0 0 20px 0;color:#333333;font-size:24px;\">Hi ").append(username).append("!</h2>");
        sb.append("<p style=\"margin:0 0 20px 0;color:#666666;font-size:16px;line-height:1.6;\">");
        sb.append("Welcome to Huddle - your new home for sharing knowledge!</p>");
        sb.append("<p style=\"margin:0 0 20px 0;color:#666666;font-size:16px;\">Here's what you can do:</p>");
        sb.append("<ul style=\"margin:0 0 20px 0;color:#666666;font-size:16px;line-height:1.8;\">");
        sb.append("<li>Write and share articles</li>");
        sb.append("<li>Comment and engage with the community</li>");
        sb.append("<li>Rate articles and discover top content</li>");
        sb.append("<li>Bookmark your favorite reads</li>");
        sb.append("<li>Follow other writers</li>");
        sb.append("</ul>");
        sb.append("<p style=\"margin:0;color:#666666;font-size:16px;\">Start by completing your profile!</p>");
        sb.append("</td></tr>");

        // Footer
        sb.append("<tr><td style=\"padding:30px 40px;background-color:#f9f9f9;border-radius:0 0 8px 8px;text-align:center;\">");
        sb.append("<p style=\"margin:0;color:#999999;font-size:12px;\">2025 Huddle. All rights reserved.</p>");
        sb.append("</td></tr>");

        sb.append("</table></td></tr></table></body></html>");

        return sb.toString();
    }
}

