package com.capstone.huddle.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Custom health indicator that checks database connectivity
 * and provides additional application info.
 */
@Component
public class CustomHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    @Value("${spring.application.name:Huddle}")
    private String applicationName;

    public CustomHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try {
            // Check database connectivity
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(5)) {
                    return Health.up()
                            .withDetail("application", applicationName)
                            .withDetail("database", "Connected")
                            .withDetail("status", "All systems operational")
                            .build();
                }
            }
            return Health.down()
                    .withDetail("application", applicationName)
                    .withDetail("database", "Connection invalid")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("application", applicationName)
                    .withDetail("database", "Disconnected")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}

