#!/bin/bash

# DocMate Platform - Start Script
echo "🚀 Starting DocMate Platform..."

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Check if .env file exists, if not copy from example
if [ ! -f .env ]; then
    echo "📋 Creating .env file from .env.example..."
    cp .env.example .env
    echo "⚠️  Please update the .env file with your actual configuration values!"
fi

# Create necessary directories
mkdir -p ./monitoring/grafana/dashboards
mkdir -p ./monitoring/grafana/provisioning

# Build all services
echo "🔨 Building all Docker images..."
docker-compose build --parallel

# Start infrastructure services first
echo "🗄️  Starting infrastructure services (Database, Redis)..."
docker-compose up -d postgres redis

# Wait for database to be ready
echo "⏳ Waiting for database to be ready..."
while ! docker-compose exec postgres pg_isready -U docmate -d docmate_platform >/dev/null 2>&1; do
    echo "   Database not ready yet, waiting 5 seconds..."
    sleep 5
done
echo "✅ Database is ready!"

# Start all application services
echo "🚀 Starting all DocMate services..."
docker-compose up -d

# Wait a moment for services to start
sleep 10

# Show service status
echo ""
echo "📊 Service Status:"
docker-compose ps

echo ""
echo "🌐 Service URLs:"
echo "   Gateway (Main API):           http://localhost:8080"
echo "   Auth Service:                 http://localhost:8081"
echo "   User Service:                 http://localhost:8082"
echo "   Appointment Service:          http://localhost:8083"
echo "   Payment Service:              http://localhost:8084"
echo "   Notification Service:         http://localhost:8085"
echo "   File Service:                 http://localhost:8086"
echo "   Prescription Service:         http://localhost:8087"
echo "   Availability Service:         http://localhost:8088"
echo "   Taxonomy Service:             http://localhost:8089"
echo "   Admin Service:                http://localhost:8090"
echo ""
echo "📈 Monitoring:"
echo "   Prometheus:                   http://localhost:9090"
echo "   Grafana:                      http://localhost:3000 (admin/admin123)"
echo ""
echo "🔍 API Documentation:"
echo "   Gateway Swagger UI:           http://localhost:8080/swagger-ui.html"
echo ""
echo "✅ DocMate Platform is starting up!"
echo "💡 Use 'docker-compose logs -f [service-name]' to view logs"
echo "💡 Use './stop.sh' to stop all services"
