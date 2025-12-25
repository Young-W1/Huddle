package com.capstone.huddle.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter to add security headers to all HTTP responses.
 * Implements OWASP security best practices.
 */
@Component
@Order(2)
public class SecurityHeadersConfig implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Prevent clickjacking attacks
        httpResponse.setHeader("X-Frame-Options", "DENY");

        // Enable XSS protection
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");

        // Prevent MIME type sniffing
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");

        // Referrer policy
        httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // Content Security Policy - adjust as needed for your frontend
        httpResponse.setHeader("Content-Security-Policy",
                "default-src 'self'; " +
                "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                "style-src 'self' 'unsafe-inline'; " +
                "img-src 'self' data: https:; " +
                "font-src 'self' data:; " +
                "connect-src 'self' http://localhost:* https://*; " +
                "frame-ancestors 'none'");

        // Permissions policy (replaces Feature-Policy)
        httpResponse.setHeader("Permissions-Policy",
                "geolocation=(), microphone=(), camera=()");

        // Strict Transport Security (for HTTPS)
        httpResponse.setHeader("Strict-Transport-Security",
                "max-age=31536000; includeSubDomains");

        // Cache control for sensitive endpoints
        httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, proxy-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");

        chain.doFilter(request, response);
    }
}

