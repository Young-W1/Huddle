package com.capstone.huddle.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiting filter to prevent abuse and protect API endpoints.
 * Uses token bucket algorithm for rate limiting per IP address.
 */
@Component
@Order(1)
public class RateLimitingConfig implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingConfig.class);

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Value("${rate.limit.requests-per-minute:60}")
    private int requestsPerMinute;

    @Value("${rate.limit.requests-per-hour:1000}")
    private int requestsPerHour;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = getClientIP(httpRequest);
        String path = httpRequest.getRequestURI();

        // Skip rate limiting for health checks and static resources
        if (shouldSkipRateLimiting(path)) {
            chain.doFilter(request, response);
            return;
        }

        Bucket bucket = buckets.computeIfAbsent(clientIp, this::createNewBucket);

        if (bucket.tryConsume(1)) {
            // Add rate limit headers
            httpResponse.setHeader("X-Rate-Limit-Remaining", String.valueOf(bucket.getAvailableTokens()));
            chain.doFilter(request, response);
        } else {
            logger.warn("Rate limit exceeded for IP: {}", clientIp);
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"success\":false,\"message\":\"Rate limit exceeded. Please try again later.\"}");
        }
    }

    private Bucket createNewBucket(String key) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(requestsPerMinute, Refill.greedy(requestsPerMinute, Duration.ofMinutes(1))))
                .addLimit(Bandwidth.classic(requestsPerHour, Refill.greedy(requestsPerHour, Duration.ofHours(1))))
                .build();
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }

    private boolean shouldSkipRateLimiting(String path) {
        return path.startsWith("/actuator") ||
               path.startsWith("/swagger") ||
               path.startsWith("/v3/api-docs") ||
               path.endsWith(".css") ||
               path.endsWith(".js") ||
               path.endsWith(".ico");
    }
}

