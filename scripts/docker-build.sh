#!/bin/bash
set -e

echo "Building Docker images..."
echo "========================="

# Build Docker images
echo "Building order-service image..."
docker build -f order-service/Dockerfile -t order-service:latest .

echo "Building inventory-service image..."
docker build -f inventory-service/Dockerfile -t inventory-service:latest .

echo "Building analytics-service image..."
docker build -f analytics-service/Dockerfile -t analytics-service:latest .

echo "Building api-gateway-service image..."
docker build -f api-gateway-service/Dockerfile -t api-gateway-service:latest .

echo ""
echo "All Docker images built successfully!"
echo "You can now start the services with: docker-compose up"
