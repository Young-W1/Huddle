package com.capstone.huddle.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Utility class for input sanitization and validation.
 * Provides methods to sanitize user input and prevent XSS/injection attacks.
 */
@Component
public class InputSanitizer {

    // Pattern to match HTML tags
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]*>");

    // Pattern to match script tags and content
    private static final Pattern SCRIPT_PATTERN = Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    // Pattern for SQL injection keywords
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            "('|\"|--)|(\\b(SELECT|INSERT|UPDATE|DELETE|DROP|UNION|ALTER|CREATE|EXEC|EXECUTE)\\b)",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Remove all HTML tags from input
     */
    public String stripHtml(String input) {
        if (input == null) {
            return null;
        }
        // First remove script tags and content
        String result = SCRIPT_PATTERN.matcher(input).replaceAll("");
        // Then remove remaining HTML tags
        return HTML_TAG_PATTERN.matcher(result).replaceAll("");
    }

    /**
     * Escape HTML special characters to prevent XSS
     */
    public String escapeHtml(String input) {
        if (input == null) {
            return null;
        }
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");
    }

    /**
     * Sanitize input for safe database storage
     * Removes potentially dangerous characters
     */
    public String sanitizeForDatabase(String input) {
        if (input == null) {
            return null;
        }
        // Remove null bytes
        String result = input.replace("\0", "");
        // Trim whitespace
        return result.trim();
    }

    /**
     * Sanitize username - only allow alphanumeric, underscore, and hyphen
     */
    public String sanitizeUsername(String username) {
        if (username == null) {
            return null;
        }
        return username.replaceAll("[^a-zA-Z0-9_-]", "").toLowerCase();
    }

    /**
     * Sanitize search query - remove special characters that could cause issues
     */
    public String sanitizeSearchQuery(String query) {
        if (query == null) {
            return null;
        }
        // Remove characters that could be used for injection
        String sanitized = query.replaceAll("[%_\\[\\]\\\\]", "");
        // Trim and limit length
        return sanitized.trim().substring(0, Math.min(sanitized.length(), 200));
    }

    /**
     * Check if input contains potential SQL injection patterns
     */
    public boolean containsSqlInjection(String input) {
        if (input == null) {
            return false;
        }
        return SQL_INJECTION_PATTERN.matcher(input).find();
    }

    /**
     * Validate email format
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    /**
     * Validate URL format
     */
    public boolean isValidUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        String urlRegex = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$";
        return Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE).matcher(url).matches();
    }

    /**
     * Sanitize file name - remove path separators and special characters
     */
    public String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return null;
        }
        // Remove path separators and special characters
        return fileName
                .replaceAll("[/\\\\:*?\"<>|]", "")
                .replaceAll("\\.\\.", "")
                .trim();
    }

    /**
     * Truncate text to specified length with ellipsis
     */
    public String truncate(String text, int maxLength) {
        if (text == null) {
            return null;
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}

