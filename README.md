# DocMate Healthcare Platform

A comprehensive microservices-based healthcare appointment booking platform that connects patients with qualified doctors. The platform supports multiple consultation modes (video, telephonic, and offline) with integrated payment processing, prescription management, and administrative controls.

## 🏗️ Architecture Overview

DocMate is built using a microservices architecture with the following components:

### Core Services
- **Gateway Service** (Port 8080) - API Gateway and routing
- **Auth Service** (Port 8081) - Authentication and authorization
- **User Service** (Port 8082) - User management (patients/doctors)
- **Appointment Service** (Port 8083) - Appointment booking and management
- **Payment Service** (Port 8084) - Payment processing with Stripe
- **Notification Service** (Port 8085) - Email/SMS notifications
- **File Service** (Port 8086) - File upload/management with AWS S3
- **Prescription Service** (Port 8087) - Prescription and medicine management
- **Availability Service** (Port 8088) - Doctor availability management
- **Taxonomy Service** (Port 8089) - Medical specializations and conditions
- **Admin Service** (Port 8090) - Administrative operations

### Infrastructure
- **PostgreSQL** - Shared database (`docmate_platform`)
- **Redis** - Caching and session management
- **Prometheus** - Metrics collection
- **Grafana** - Monitoring dashboards

## 🚀 Quick Start (Docker - Recommended)

### Prerequisites
- Docker and Docker Compose installed
- At least 8GB RAM available
- Ports 5432, 6379, 8080-8090, 3000, 9090 available

### 1. Clone and Setup
```bash
git clone <repository-url>
cd docmate-platform
```

### 2. Configure Environment
```bash
# Copy environment template
cp docker/.env.example docker/.env

# Edit the configuration file
nano docker/.env
```

### 3. Start the Platform
```bash
cd docker
./start.sh
```

The startup script will:
- Build all Docker images
- Start infrastructure services (PostgreSQL, Redis)
- Wait for database readiness
- Start all microservices
- Display service URLs and status

### 4. Access the Platform
- **Main API Gateway**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Grafana Dashboard**: http://localhost:3000 (admin/admin123)
- **Prometheus Metrics**: http://localhost:9090

### 5. Stop the Platform
```bash
./stop.sh
```

## 🛠️ Local Development Setup

### Prerequisites
- Java 17 or higher
- Maven 3.8+
- PostgreSQL 14+
- Redis 6+
- Node.js 16+ (for any frontend components)

### 1. Database Setup

#### Using Docker (Recommended)
```bash
# Start PostgreSQL container
docker run -itd \
  -e POSTGRES_USER=docmate \
  -e POSTGRES_PASSWORD=password \
  -e POSTGRES_DB=docmate_platform \
  -p 5432:5432 \
  -v ./data:/var/lib/postgresql/data \
  --name postgresql postgres:15

# Start Redis container
docker run -itd \
  -p 6379:6379 \
  --name redis redis:7-alpine
```

#### Manual Installation
```bash
# PostgreSQL
createdb -U postgres docmate_platform

# Update connection settings in each service's application.yml
# Default configuration:
# url: jdbc:postgresql://localhost:5432/docmate_platform
# username: docmate
# password: password
```

### 2. Environment Configuration

Create environment variables or update `application.yml` files with:

```yaml
# Database Configuration
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/docmate_platform
    username: docmate
    password: password

# JWT Configuration
app:
  jwt:
    secret: docmate-secret-key-for-jwt-token-generation-2024
    expiration: 86400000
    refresh-expiration: 604800000
```

### 3. Build the Project
```bash
# Build all modules
mvn clean compile

# Or build and package
mvn clean package -DskipTests
```

### 4. Start Services Individually

#### Option A: Using Maven (Development)
```bash
# Start services in separate terminals

# 1. Start Auth Service
cd auth-service
mvn spring-boot:run

# 2. Start User Service
cd user-service
mvn spring-boot:run

# 3. Start other services...
# Continue with other services as needed
```

#### Option B: Using JAR files
```bash
# After building with mvn clean package

# Start Auth Service
java -jar auth-service/target/auth-service-0.0.1-SNAPSHOT.jar

# Start User Service
java -jar user-service/target/user-service-0.0.1-SNAPSHOT.jar

# Continue with other services...
```

### 5. Service Startup Order (if starting manually)
1. **Infrastructure**: PostgreSQL, Redis
2. **Core Services**: Auth Service, User Service
3. **Business Services**: Appointment, Payment, Notification, etc.
4. **Gateway**: API Gateway (last)

## 🏁 How to Start DocMate Locally

Follow these steps to run the platform locally with proper configuration:

### 1. Clone the Repository
```bash
git clone <repository-url>
cd docmate-platform
```

### 2. Prepare Local Configuration

- Ensure you have Java 17+, Maven 3.8+, PostgreSQL 14+, Redis 6+, Node.js 16+ (if using frontend).
- Copy and edit environment files as needed:
  - For Docker: `cp docker/.env.example docker/.env`
  - For local: Edit each service's `src/main/resources/application.yml` with your DB, Redis, and external service credentials.

#### Example `application.yml` snippet:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/docmate_platform
    username: docmate
    password: password
app:
  jwt:
    secret: docmate-secret-key-for-jwt-token-generation-2024
    expiration: 86400000
    refresh-expiration: 604800000
```

### 3. Start Infrastructure

#### Using Docker:
```bash
docker run -itd \
  -e POSTGRES_USER=docmate \
  -e POSTGRES_PASSWORD=password \
  -e POSTGRES_DB=docmate_platform \
  -p 5432:5432 \
  --name postgresql postgres:15

docker run -itd \
  -p 6379:6379 \
  --name redis redis:7-alpine
```

#### Or manually start PostgreSQL and Redis.

### 4. Build All Modules
```bash
mvn clean package -DskipTests
```

### 5. Run Each Service Locally

Open a terminal for each service and run:

```bash
cd <service-folder>
mvn spring-boot:run
```
Or, using the JAR:
```bash
java -jar <service-folder>/target/<service-name>-0.0.1-SNAPSHOT.jar
```

#### Example for all modules:
```bash
cd gateway-service         && mvn spring-boot:run
cd auth-service           && mvn spring-boot:run
cd user-service           && mvn spring-boot:run
cd appointment-service    && mvn spring-boot:run
cd payment-service        && mvn spring-boot:run
cd notification-service   && mvn spring-boot:run
cd file-service           && mvn spring-boot:run
cd prescription-service   && mvn spring-boot:run
cd availability-service   && mvn spring-boot:run
cd taxonomy-service       && mvn spring-boot:run
cd admin-service          && mvn spring-boot:run
```

### 6. Verify Services

Each service exposes health endpoints:
```bash
curl http://localhost:<port>/actuator/health
curl http://localhost:<port>/actuator/info
curl http://localhost:<port>/actuator/metrics
```
Replace `<port>` with the service port (see Architecture Overview).

### 7. Access the Platform

- API Gateway: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Individual Swagger: http://localhost:808X/swagger-ui.html

### 8. Run and Verify All Modules

#### Build and Test All Modules
```bash
mvn clean package
mvn test
mvn verify
```

#### Run Integration Tests for a Module
```bash
cd <service-folder>
mvn verify
```

#### Check Logs
```bash
# For Maven
tail -f <service-folder>/logs/*.log

# For Docker
docker logs -f <container-name>
```

### 9. External Service Configuration

Set environment variables for AWS S3, Stripe, SendGrid, Twilio as needed (see Configuration section below).

## ⚡ Module-by-Module Commands

| Module                | Folder Name           | Port   | Start Command                | Health Check Command                       |
|-----------------------|----------------------|--------|------------------------------|--------------------------------------------|
| Gateway Service       | gateway-service      | 8080   | mvn spring-boot:run          | curl http://localhost:8080/actuator/health |
| Auth Service          | auth-service         | 8081   | mvn spring-boot:run          | curl http://localhost:8081/actuator/health |
| User Service          | user-service         | 8082   | mvn spring-boot:run          | curl http://localhost:8082/actuator/health |
| Appointment Service   | appointment-service  | 8083   | mvn spring-boot:run          | curl http://localhost:8083/actuator/health |
| Payment Service       | payment-service      | 8084   | mvn spring-boot:run          | curl http://localhost:8084/actuator/health |
| Notification Service  | notification-service | 8085   | mvn spring-boot:run          | curl http://localhost:8085/actuator/health |
| File Service          | file-service         | 8086   | mvn spring-boot:run          | curl http://localhost:8086/actuator/health |
| Prescription Service  | prescription-service | 8087   | mvn spring-boot:run          | curl http://localhost:8087/actuator/health |
| Availability Service  | availability-service | 8088   | mvn spring-boot:run          | curl http://localhost:8088/actuator/health |
| Taxonomy Service      | taxonomy-service     | 8089   | mvn spring-boot:run          | curl http://localhost:8089/actuator/health |
| Admin Service         | admin-service        | 8090   | mvn spring-boot:run          | curl http://localhost:8090/actuator/health |

- To run all: open a terminal for each, navigate to the folder, and run the command.
- To verify: use the health check command for each service.

## ⚙️ Configuration

### Required External Services

#### AWS S3 (File Storage)
```bash
export AWS_ACCESS_KEY_ID=your_access_key
export AWS_SECRET_ACCESS_KEY=your_secret_key
export AWS_REGION=us-east-1
export AWS_S3_BUCKET_NAME=docmate-files
```

#### Stripe (Payments)
```bash
export STRIPE_SECRET_KEY=sk_test_your_stripe_secret_key
export STRIPE_WEBHOOK_SECRET=whsec_your_webhook_secret
```

#### SendGrid (Email)
```bash
export SENDGRID_API_KEY=your_sendgrid_api_key
export APP_EMAIL_FROM=noreply@docmate.com
```

#### Twilio (SMS)
```bash
export TWILIO_ACCOUNT_SID=your_twilio_account_sid
export TWILIO_AUTH_TOKEN=your_twilio_auth_token
export TWILIO_PHONE_NUMBER=+1234567890
```

### Service-Specific Configuration

Each service can be configured via environment variables or `application.yml`:

| Service | Key Configurations |
|---------|-------------------|
| **Auth Service** | JWT secrets, Redis connection |
| **Payment Service** | Stripe API keys |
| **Notification Service** | SendGrid, Twilio credentials |
| **File Service** | AWS S3 credentials |
| **All Services** | Database connection, logging levels |

## 📊 Monitoring and Health Checks

### Health Endpoints
Each service exposes health check endpoints:
- `GET /actuator/health` - Service health status
- `GET /actuator/info` - Service information
- `GET /actuator/metrics` - Prometheus metrics

### Monitoring Stack (Docker only)
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin123)

### Logs
```bash
# Docker Compose logs
docker-compose logs -f [service-name]

# Individual service logs
docker logs -f docmate-[service-name]

# All services
docker-compose logs -f
```

## 📖 API Documentation

### Swagger UI
- **Gateway**: http://localhost:8080/swagger-ui.html
- **Individual Services**: http://localhost:808X/swagger-ui.html

### Key API Endpoints

#### Authentication
```
POST /api/auth/login
POST /api/auth/register/patient
POST /api/auth/register/doctor
GET  /api/auth/me
POST /api/auth/refresh
```

#### Appointments
```
GET    /api/appointments
POST   /api/appointments
GET    /api/appointments/{id}
PUT    /api/appointments/{id}/status
```

#### Doctors
```
GET    /api/doctors/search
GET    /api/doctors/me/profile
PUT    /api/doctors/me/profile
GET    /api/doctors/me/stats
```

## 🔧 Development

### Hot Reload
```bash
# Using Spring Boot DevTools (enabled in development)
mvn spring-boot:run

# The application will automatically restart when code changes
```

### Database Migrations
Flyway is configured for database migrations:
```bash
# Migrations are automatically applied on startup
# Migration files location: src/main/resources/db/migration/
```

### Testing
```bash
# Run all tests
mvn test

# Run tests for specific service
cd user-service && mvn test

# Integration tests
mvn verify
```

## 🐛 Troubleshooting

### Common Issues

#### Database Connection Issues
```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# Check database logs
docker logs docmate-postgres

# Test connection
psql -h localhost -U docmate -d docmate_platform
```

#### Service Not Starting
```bash
# Check if ports are available
lsof -i :8081

# Check service logs
docker logs docmate-auth-service

# Check health endpoint
curl http://localhost:8081/actuator/health
```

#### Build Failures
```bash
# Clean and rebuild
mvn clean compile

# Check for dependency issues
mvn dependency:tree

# Update Maven dependencies
mvn versions:display-plugin-updates
```

### Port Conflicts
If default ports are occupied, update the following:
- Docker Compose: Edit `docker/docker-compose.yml`
- Local Development: Update `server.port` in `application.yml`

### Memory Issues
- **Docker**: Increase Docker memory limit (8GB+ recommended)
- **Local**: Increase JVM heap size with `-Xmx2g`

---

**Happy Coding! 🏥💻**
