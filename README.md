# Chat Application Backend

A microservices-based chat application built with Spring Boot.

## Services

- **Gateway Service** (Port 8080) - API Gateway and routing
- **Auth Service** (Port 8081) - Authentication and authorization
- **User Service** (Port 8082) - User management
- **Chat Service** (Port 8083) - Chat and messaging
- **WebSocket Service** (Port 8084) - Real-time communication
- **Notification Service** (Port 8085) - Push notifications
- **Media Service** (Port 8086) - File uploads and media handling
- **Common Library** - Shared utilities and DTOs

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker and Docker Compose

## Quick Start

1. **Start Infrastructure Services**:
   ```bash
   docker-compose up -d postgres redis mongodb kafka zookeeper
   ```

2. **Build the Project**:
   ```bash
   mvn clean install
   ```

3. **Start Services** (in separate terminals):
   ```bash
   # Start each service
   cd chat-gateway && mvn spring-boot:run
   cd chat-auth-service && mvn spring-boot:run
   cd chat-user-service && mvn spring-boot:run
   cd chat-service && mvn spring-boot:run
   cd chat-websocket-service && mvn spring-boot:run
   cd chat-notification-service && mvn spring-boot:run
   cd chat-media-service && mvn spring-boot:run
   ```

## Health Checks

Check if services are running:

```bash
curl http://localhost:8080/actuator/health  # Gateway
curl http://localhost:8081/actuator/health  # Auth
curl http://localhost:8082/actuator/health  # User
curl http://localhost:8083/actuator/health  # Chat
curl http://localhost:8084/actuator/health  # WebSocket
curl http://localhost:8085/actuator/health  # Notification
curl http://localhost:8086/actuator/health  # Media
```

## Database Access

- **PostgreSQL**: localhost:5432 (username: chatapp, password: password)
- **PgAdmin**: http://localhost:5050 (admin@chatapp.com / admin123)
- **MongoDB**: localhost:27017 (username: chatapp, password: password)
- **Mongo Express**: http://localhost:8081
- **Redis**: localhost:6379

## Development

Each service can be developed independently. The common library contains shared utilities and should be built first.

```bash
# Build common library first
cd chat-common && mvn install
```

## Architecture

The application follows a microservices architecture with:
- API Gateway for request routing
- Separate databases per service (when needed)
- Event-driven communication via Kafka
- Real-time communication via WebSocket
- Shared common library for utilities

## Next Steps

1. Implement JWT authentication in auth service
2. Add service discovery (Eureka)
3. Implement actual chat functionality
4. Add comprehensive logging
5. Set up monitoring and metrics
6. Add integration tests
