#!/bin/bash
set -e

echo "Deploying Orion Platform to Kubernetes..."
echo "=========================================="

# Check if minikube is running
if ! minikube status > /dev/null 2>&1; then
    echo "Starting Minikube..."
    minikube start --cpus=4 --memory=8192
fi

# Use Minikube's Docker daemon
echo "Configuring Docker to use Minikube's daemon..."
eval $(minikube docker-env)

# Build Docker images
echo "Building Docker images in Minikube..."
docker build -f order-service/Dockerfile -t order-service:latest .
docker build -f inventory-service/Dockerfile -t inventory-service:latest .
docker build -f analytics-service/Dockerfile -t analytics-service:latest .
docker build -f api-gateway-service/Dockerfile -t api-gateway-service:latest .

# Deploy to Kubernetes
echo "Deploying to Kubernetes..."
kubectl apply -f k8s/

# Wait for deployments
echo "Waiting for deployments to be ready..."
kubectl wait --for=condition=ready pod -l app=postgres-order -n orion-platform --timeout=300s
kubectl wait --for=condition=ready pod -l app=postgres-inventory -n orion-platform --timeout=300s
kubectl wait --for=condition=ready pod -l app=mongodb -n orion-platform --timeout=300s
kubectl wait --for=condition=ready pod -l app=inventory-service -n orion-platform --timeout=300s
kubectl wait --for=condition=ready pod -l app=analytics-service -n orion-platform --timeout=300s
kubectl wait --for=condition=ready pod -l app=order-service -n orion-platform --timeout=300s
kubectl wait --for=condition=ready pod -l app=api-gateway -n orion-platform --timeout=300s

# Get service URL
echo ""
echo "Deployment completed successfully!"
echo "==================================="
echo ""
echo "Access the API Gateway at:"
MINIKUBE_IP=$(minikube ip)
echo "http://${MINIKUBE_IP}:30080"
echo ""
echo "Swagger UI:"
echo "http://${MINIKUBE_IP}:30080/swagger-ui.html"
echo ""
echo "Check pod status with:"
echo "kubectl get pods -n orion-platform"
