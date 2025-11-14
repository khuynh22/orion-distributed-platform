# Orion Platform - Quick Start Guide

This guide will help you get the Orion Distributed Platform up and running in minutes.

## Prerequisites

Make sure you have installed:
- **Java 17** or higher
- **Maven 3.8+**
- **Docker** and **Docker Compose**

## Option 1: Run with Docker Compose (Recommended for beginners)

### Step 1: Build the Project

```bash
# Build all Maven modules
./scripts/build-all.sh
```

### Step 2: Start All Services

```bash
# Start all services with Docker Compose
docker-compose up --build
```

This will start:
- PostgreSQL (for orders and inventory)
- MongoDB (for analytics)
- Order Service
- Inventory Service
- Analytics Service
- API Gateway

### Step 3: Access the API

Once all services are running, you can access:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Base URL**: http://localhost:8080/api

### Step 4: Test the API

Try these endpoints:

```bash
# Get inventory
curl http://localhost:8080/api/inventory/SKU-001

# Create an order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "customer-123",
    "items": [
      {
        "sku": "SKU-001",
        "quantity": 2,
        "price": 29.99
      }
    ]
  }'

# Get analytics
curl http://localhost:8080/api/analytics/summary
```

Or use the automated test script:

```bash
./scripts/test-api.sh
```

### Step 5: Stop Services

```bash
docker-compose down
```

## Option 2: Run with Kubernetes (Minikube)

### Step 1: Start Minikube

```bash
minikube start --cpus=4 --memory=8192
```

### Step 2: Deploy the Platform

```bash
./scripts/k8s-deploy.sh
```

This script will:
1. Build all Docker images in Minikube's Docker daemon
2. Deploy all services to Kubernetes
3. Wait for all pods to be ready
4. Display the API Gateway URL

### Step 3: Access the API

Get the Minikube IP:

```bash
minikube ip
```

Then access:
- **API Gateway**: http://[MINIKUBE-IP]:30080/api
- **Swagger UI**: http://[MINIKUBE-IP]:30080/swagger-ui.html

### Step 4: Monitor Services

```bash
# Check pod status
kubectl get pods -n orion-platform

# View logs
kubectl logs -n orion-platform -l app=api-gateway

# Port forward to local machine (alternative access method)
kubectl port-forward -n orion-platform svc/api-gateway 8080:8080
```

### Step 5: Clean Up

```bash
./scripts/k8s-cleanup.sh
minikube stop
```

## Troubleshooting

### Docker Compose Issues

**Services not starting?**
```bash
# Check logs
docker-compose logs -f

# Restart a specific service
docker-compose restart order-service
```

**Database connection issues?**
```bash
# Verify databases are running
docker-compose ps

# Check database logs
docker-compose logs postgres-order
docker-compose logs mongodb
```

### Kubernetes Issues

**Pods not starting?**
```bash
# Check pod status
kubectl get pods -n orion-platform

# Describe a pod to see events
kubectl describe pod [POD-NAME] -n orion-platform

# Check logs
kubectl logs [POD-NAME] -n orion-platform
```

**Image pull errors?**
```bash
# Make sure you're using Minikube's Docker daemon
eval $(minikube docker-env)

# Rebuild images
./scripts/k8s-deploy.sh
```

## Next Steps

- Explore the **[Main README](README.md)** for detailed documentation
- Check out **[API Documentation](api-gateway-service/README.md)**
- Read about individual services:
  - [Order Service](order-service/README.md)
  - [Inventory Service](inventory-service/README.md)
  - [Analytics Service](analytics-service/README.md)

## Architecture Overview

```
Client → API Gateway (REST) → Internal Services (gRPC) → Databases
```

- **API Gateway**: REST API on port 8080
- **Order Service**: Manages orders (gRPC: 50051)
- **Inventory Service**: Tracks inventory (gRPC: 50052)
- **Analytics Service**: Aggregates analytics (gRPC: 50053)

## Sample API Calls

### Create Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "cust-001",
    "items": [
      {"sku": "SKU-001", "quantity": 2, "price": 29.99},
      {"sku": "SKU-002", "quantity": 1, "price": 49.99}
    ]
  }'
```

### Get Order
```bash
curl http://localhost:8080/api/orders/{order-id}
```

### Check Inventory
```bash
curl http://localhost:8080/api/inventory/SKU-001
```

### Get Analytics
```bash
curl http://localhost:8080/api/analytics/summary?startDate=0&endDate=9999999999999
```

## Available Sample SKUs

The platform comes with pre-populated inventory:

- **SKU-001**: 100 units
- **SKU-002**: 200 units
- **SKU-003**: 150 units
- **SKU-004**: 75 units
- **SKU-005**: 300 units

Happy coding! 🚀
