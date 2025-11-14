#!/bin/bash
set -e

echo "Cleaning up Orion Platform from Kubernetes..."
echo "=============================================="

# Delete namespace (this will delete all resources)
kubectl delete namespace orion-platform

echo ""
echo "Cleanup completed successfully!"
