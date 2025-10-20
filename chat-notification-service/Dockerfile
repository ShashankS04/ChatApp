FROM openjdk:17-jdk-slim

LABEL maintainer="chat-app-team"

WORKDIR /app

# Copy the jar file
COPY target/chat-notification-service-*.jar app.jar

# Expose the port
EXPOSE 8085

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8085/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
