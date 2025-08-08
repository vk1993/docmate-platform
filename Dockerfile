# Multi-stage Dockerfile for DocMate Platform Services
# This Dockerfile can be used for all service modules

# Build stage - Use Maven image with Amazon Corretto
FROM maven:3.9.11-amazoncorretto-17 AS build

# Set working directory
WORKDIR /app

# Copy parent pom.xml first for better caching
COPY pom.xml .

# Copy all modules (needed for parent POM resolution)
COPY common/ ./common/
COPY gateway/ ./gateway/
COPY db-migration/ ./db-migration/
COPY admin-service/ ./admin-service/
COPY auth-service/ ./auth-service/
COPY user-service/ ./user-service/
COPY appointment-service/ ./appointment-service/
COPY availability-service/ ./availability-service/
COPY file-service/ ./file-service/
COPY notification-service/ ./notification-service/
COPY payment-service/ ./payment-service/
COPY prescription-service/ ./prescription-service/
COPY taxonomy-service/ ./taxonomy-service/

# Build the specific service - Spring Boot plugin will automatically repackage
ARG SERVICE_NAME
RUN mvn clean package -pl ${SERVICE_NAME} -am -DskipTests

# Runtime stage
FROM amazoncorretto:17-alpine

# Install curl for health checks
RUN apk add --no-cache curl

# Create app user for security
RUN addgroup -g 1001 -S appuser && adduser -u 1001 -S appuser -G appuser

# Set working directory
WORKDIR /app

# Copy the built JAR file
ARG SERVICE_NAME
COPY --from=build /app/${SERVICE_NAME}/target/${SERVICE_NAME}-*.jar app.jar

# Change ownership to appuser
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port (will be overridden by docker-compose)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application with optimized JVM settings for containers
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+UseG1GC", \
    "-XX:+UseStringDeduplication", \
    "-jar", "app.jar"]
