# Orion Distributed Platform

A production-ready, distributed Java microservice platform demonstrating modern backend architecture with gRPC, REST APIs, and Kubernetes orchestration.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green)
![gRPC](https://img.shields.io/badge/gRPC-1.60.0-blue)
![Kubernetes](https://img.shields.io/badge/Kubernetes-Ready-326CE5)
![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED)

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Running with Docker Compose](#running-with-docker-compose)
- [Running with Kubernetes](#running-with-kubernetes)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [CI/CD](#cicd)

## 🎯 Overview

Orion is a complete distributed microservice platform that demonstrates:

- **REST API Gateway** for external client communication
- **gRPC** for high-performance internal service-to-service communication
- **Relational Database** (PostgreSQL) for transactional data
- **NoSQL Database** (MongoDB) for analytics and aggregation
- **Docker containerization** for all services
- **Kubernetes orchestration** with complete manifests
- **Automated CI/CD** pipeline with GitHub Actions

## 🏗️ Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                         Clients                               │
└────────────────────────┬─────────────────────────────────────┘
                         │ REST/HTTP
                         ▼
                ┌────────────────────┐
                │   API Gateway      │
                │   (Spring Boot)    │
                │   Port: 8080       │
                └────────┬───────────┘
                         │ gRPC
        ┌────────────────┼────────────────┐
        │                │                │
        ▼                ▼                ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│Order Service │  │Inventory Svc │  │Analytics Svc │
│   gRPC       │  │   gRPC       │  │   gRPC       │
│Port: 50051   │  │Port: 50052   │  │Port: 50053   │
└──────┬───────┘  └──────┬───────┘  └──────┬───────┘
       │                 │                 │
       ▼                 ▼                 ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│  PostgreSQL  │  │  PostgreSQL  │  │   MongoDB    │
│   orderdb    │  │ inventorydb  │  │  analytics   │
└──────────────┘  └──────────────┘  └──────────────┘
```

### Service Responsibilities

#### API Gateway Service
- Public REST API endpoints
- Routes requests to internal services via gRPC
- Swagger/OpenAPI documentation
- Port: 8080 (HTTP)

#### Order Service
- Create and manage orders
- Store order data in PostgreSQL
- Communicate with Inventory and Analytics services
- Ports: 8081 (HTTP), 50051 (gRPC)

#### Inventory Service
- Track product inventory
- Adjust stock levels
- Store inventory data in PostgreSQL
- Ports: 8082 (HTTP), 50052 (gRPC)

#### Analytics Service
- Aggregate order events
- Generate analytics reports
- Store summaries in MongoDB
- Ports: 8083 (HTTP), 50053 (gRPC)

## 🛠️ Technologies

### Core Technologies
- **Java 17** - Programming language
- **Spring Boot 3.2.0** - Application framework
- **gRPC 1.60.0** - RPC framework for service communication
- **Protocol Buffers** - Data serialization

### Databases
- **PostgreSQL 15** - Relational database
- **MongoDB 7** - NoSQL document database

### Infrastructure
- **Docker** - Containerization
- **Docker Compose** - Local orchestration
- **Kubernetes** - Production orchestration
- **Minikube** - Local Kubernetes cluster

### Testing
- **JUnit 5** - Unit testing
- **Mockito** - Mocking framework
- **Testcontainers** - Integration testing with real databases

### Build & CI/CD
- **Maven** - Build tool
- **GitHub Actions** - CI/CD pipeline
- **Hadolint** - Dockerfile linting
- **Kubeval** - Kubernetes manifest validation

## 📁 Project Structure

```
orion-distributed-platform/
├── shared-protos/              # Shared gRPC proto definitions
│   └── src/main/proto/
│       ├── order.proto
│       ├── inventory.proto
│       └── analytics.proto
├── order-service/              # Order management service
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
├── inventory-service/          # Inventory management service
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── analytics-service/          # Analytics aggregation service
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── api-gateway-service/        # REST API Gateway
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── k8s/                        # Kubernetes manifests
│   ├── namespace.yml
│   ├── postgres-order.yml
│   ├── postgres-inventory.yml
│   ├── mongodb.yml
│   ├── order-service.yml
│   ├── inventory-service.yml
│   ├── analytics-service.yml
│   ├── api-gateway.yml
│   └── ingress.yml
├── .github/workflows/          # CI/CD pipeline
│   └── ci.yml
├── docker-compose.yml          # Docker Compose configuration
└── pom.xml                     # Parent Maven POM
```

## 🚀 Getting Started

### Prerequisites

- **Java 17** or higher
- **Maven 3.8+**
- **Docker** and **Docker Compose**
- **kubectl** and **Minikube** (for Kubernetes)

### Build the Project

```bash
# Build all services
mvn clean install

# Build individual services
mvn -f shared-protos/pom.xml clean install
mvn -f order-service/pom.xml clean package
mvn -f inventory-service/pom.xml clean package
mvn -f analytics-service/pom.xml clean package
mvn -f api-gateway-service/pom.xml clean package
```

## 🐳 Running with Docker Compose

### Start All Services

```bash
# Build and start all services
docker-compose up --build

# Start in detached mode
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### Verify Services

```bash
# Check running containers
docker-compose ps

# API Gateway health
curl http://localhost:8080/actuator/health

# Swagger UI
open http://localhost:8080/swagger-ui.html
```

## ☸️ Running with Kubernetes

### Setup Minikube

```bash
# Start Minikube
minikube start --cpus=4 --memory=8192

# Enable ingress
minikube addons enable ingress

# Use Minikube's Docker daemon
eval $(minikube docker-env)
```

### Build Docker Images for Kubernetes

```bash
# Build all images
docker build -f order-service/Dockerfile -t order-service:latest .
docker build -f inventory-service/Dockerfile -t inventory-service:latest .
docker build -f analytics-service/Dockerfile -t analytics-service:latest .
docker build -f api-gateway-service/Dockerfile -t api-gateway-service:latest .
```

### Deploy to Kubernetes

```bash
# Create namespace and deploy all resources
kubectl apply -f k8s/

# Check deployment status
kubectl get all -n orion-platform

# Watch pods starting
kubectl get pods -n orion-platform -w

# Check logs
kubectl logs -n orion-platform -l app=api-gateway
```

### Access the API Gateway

```bash
# Get Minikube IP
minikube ip

# Get NodePort
kubectl get svc api-gateway -n orion-platform

# Access API (replace <MINIKUBE-IP> with actual IP)
curl http://<MINIKUBE-IP>:30080/swagger-ui.html
```

### Clean Up

```bash
# Delete all resources
kubectl delete namespace orion-platform

# Stop Minikube
minikube stop
```

## 📚 API Documentation

### REST Endpoints (API Gateway)

The API Gateway exposes Swagger UI at: `http://localhost:8080/swagger-ui.html`

#### Create Order
```bash
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
```

#### Get Order
```bash
curl http://localhost:8080/api/orders/{orderId}
```

#### Get Inventory
```bash
curl http://localhost:8080/api/inventory/SKU-001
```

#### Get Analytics Summary
```bash
curl http://localhost:8080/api/analytics/summary?startDate=0&endDate=9999999999999
```

### gRPC Endpoints

Test gRPC services using `grpcurl`:

```bash
# Install grpcurl
brew install grpcurl  # macOS
# or download from: https://github.com/fullstorydev/grpcurl

# List services
grpcurl -plaintext localhost:50051 list

# Create order
grpcurl -plaintext -d '{
  "customer_id": "customer-123",
  "items": [
    {"sku": "SKU-001", "quantity": 2, "price": 29.99}
  ]
}' localhost:50051 order.OrderService/CreateOrder

# Get order
grpcurl -plaintext -d '{"order_id": "order-uuid"}' \
  localhost:50051 order.OrderService/GetOrder

# Get inventory
grpcurl -plaintext -d '{"sku": "SKU-001"}' \
  localhost:50052 inventory.InventoryService/GetInventory

# Get analytics
grpcurl -plaintext -d '{"start_date": 0, "end_date": 9999999999999}' \
  localhost:50053 analytics.AnalyticsService/GetOrderSummary
```

## 🧪 Testing

### Run Unit Tests

```bash
# Run all tests
mvn test

# Run tests for specific service
mvn -f order-service/pom.xml test
```

### Run Integration Tests

Integration tests use Testcontainers to spin up real databases:

```bash
# Ensure Docker is running
docker ps

# Run integration tests
mvn verify
```

## 🔄 CI/CD

The project includes a GitHub Actions workflow (`.github/workflows/ci.yml`) that:

1. **Build & Test**: Compiles all services and runs tests
2. **Docker Build**: Creates Docker images for all services
3. **Lint**: Validates Dockerfiles and Kubernetes manifests

### Workflow Triggers

- Push to `main` or `develop` branches
- Pull requests to `main` or `develop`

### Environment Variables

No secrets required for basic CI/CD. For image registry push:

```yaml
# Add to GitHub Secrets
GHCR_TOKEN: <your-github-token>
```

## 📖 Additional Documentation

Each service has its own README with detailed information:

- [Order Service Documentation](order-service/README.md)
- [Inventory Service Documentation](inventory-service/README.md)
- [Analytics Service Documentation](analytics-service/README.md)
- [API Gateway Documentation](api-gateway-service/README.md)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

Built as a demonstration of modern distributed system architecture.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- gRPC community for high-performance RPC
- Kubernetes community for container orchestration