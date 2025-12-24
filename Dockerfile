# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -Pproduction

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Create uploads directory
RUN mkdir -p /app/uploads && chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port (Render will set PORT env var)
EXPOSE 8080

# Health check - increased start period for free tier cold starts
HEALTHCHECK --interval=30s --timeout=10s --start-period=120s --retries=5 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

# Run the application with optimized JVM settings for free tier
ENTRYPOINT ["java", "-Xmx384m", "-Xms128m", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Dspring.main.lazy-initialization=true", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]

