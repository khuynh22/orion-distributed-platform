#!/bin/bash
set -e

echo "Building Orion Distributed Platform..."
echo "======================================="

# Build all Maven modules
echo "Building Maven modules..."
mvn clean install -DskipTests

echo ""
echo "Build completed successfully!"
echo "You can now run the services with Docker Compose or deploy to Kubernetes."
