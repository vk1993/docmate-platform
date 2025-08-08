#!/bin/bash

# DocMate Platform - Stop Script
echo "🛑 Stopping DocMate Platform..."

# Stop all services
echo "📦 Stopping all containers..."
docker-compose down

# Optional: Remove volumes (uncomment if you want to reset data)
# echo "🗑️  Removing volumes..."
# docker-compose down -v

# Optional: Remove images (uncomment if you want to clean up completely)
# echo "🧹 Removing images..."
# docker-compose down --rmi all

echo "✅ DocMate Platform stopped successfully!"
echo ""
echo "💡 To restart: ./start.sh"
echo "💡 To reset all data: docker-compose down -v"
echo "💡 To clean up completely: docker-compose down -v --rmi all"
