#!/bin/bash

# Get the API Gateway URL
if command -v minikube &> /dev/null; then
    API_URL="http://$(minikube ip):30080/api"
else
    API_URL="http://localhost:8080/api"
fi

echo "Testing Orion Platform API..."
echo "=============================="
echo "API URL: $API_URL"
echo ""

# Test 1: Get inventory
echo "1. Getting inventory for SKU-001..."
curl -s "${API_URL}/inventory/SKU-001" | jq .
echo ""

# Test 2: Create an order
echo "2. Creating a new order..."
ORDER_RESPONSE=$(curl -s -X POST "${API_URL}/orders" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "customer-123",
    "items": [
      {
        "sku": "SKU-001",
        "quantity": 2,
        "price": 29.99
      },
      {
        "sku": "SKU-002",
        "quantity": 1,
        "price": 49.99
      }
    ]
  }')

echo "$ORDER_RESPONSE" | jq .
ORDER_ID=$(echo "$ORDER_RESPONSE" | jq -r '.orderId')
echo ""

# Test 3: Get the created order
echo "3. Getting order $ORDER_ID..."
curl -s "${API_URL}/orders/${ORDER_ID}" | jq .
echo ""

# Test 4: Check updated inventory
echo "4. Checking updated inventory for SKU-001..."
curl -s "${API_URL}/inventory/SKU-001" | jq .
echo ""

# Test 5: Get analytics summary
echo "5. Getting analytics summary..."
curl -s "${API_URL}/analytics/summary" | jq .
echo ""

echo "API testing completed!"
