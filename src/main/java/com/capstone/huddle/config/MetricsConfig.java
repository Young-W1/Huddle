package com.capstone.huddle.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Custom metrics configuration for application observability.
 * Provides counters and timers for key business metrics.
 */
@Configuration
public class MetricsConfig {

    private final MeterRegistry meterRegistry;

    public MetricsConfig(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    // ===== Article Metrics =====

    @Bean
    public Counter articleCreatedCounter() {
        return Counter.builder("huddle.articles.created")
                .description("Number of articles created")
                .register(meterRegistry);
    }

    @Bean
    public Counter articleViewedCounter() {
        return Counter.builder("huddle.articles.viewed")
                .description("Number of article views")
                .register(meterRegistry);
    }

    @Bean
    public Counter articleDeletedCounter() {
        return Counter.builder("huddle.articles.deleted")
                .description("Number of articles deleted")
                .register(meterRegistry);
    }

    // ===== User Metrics =====

    @Bean
    public Counter userRegistrationCounter() {
        return Counter.builder("huddle.users.registered")
                .description("Number of user registrations")
                .register(meterRegistry);
    }

    @Bean
    public Counter userLoginCounter() {
        return Counter.builder("huddle.users.login")
                .description("Number of user logins")
                .tag("status", "success")
                .register(meterRegistry);
    }

    @Bean
    public Counter userLoginFailedCounter() {
        return Counter.builder("huddle.users.login")
                .description("Number of failed login attempts")
                .tag("status", "failed")
                .register(meterRegistry);
    }

    // ===== Comment Metrics =====

    @Bean
    public Counter commentCreatedCounter() {
        return Counter.builder("huddle.comments.created")
                .description("Number of comments created")
                .register(meterRegistry);
    }

    // ===== Report Metrics =====

    @Bean
    public Counter reportCreatedCounter() {
        return Counter.builder("huddle.reports.created")
                .description("Number of reports created")
                .register(meterRegistry);
    }

    @Bean
    public Counter reportResolvedCounter() {
        return Counter.builder("huddle.reports.resolved")
                .description("Number of reports resolved")
                .register(meterRegistry);
    }

    // ===== Search Metrics =====

    @Bean
    public Counter searchRequestCounter() {
        return Counter.builder("huddle.search.requests")
                .description("Number of search requests")
                .register(meterRegistry);
    }

    @Bean
    public Timer searchResponseTimer() {
        return Timer.builder("huddle.search.response.time")
                .description("Search response time")
                .register(meterRegistry);
    }

    // ===== API Request Metrics =====

    @Bean
    public Counter apiErrorCounter() {
        return Counter.builder("huddle.api.errors")
                .description("Number of API errors")
                .register(meterRegistry);
    }

    @Bean
    public Counter rateLimitExceededCounter() {
        return Counter.builder("huddle.rate.limit.exceeded")
                .description("Number of rate limit exceeded events")
                .register(meterRegistry);
    }
}

