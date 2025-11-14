# API Gateway Service

The API Gateway provides a unified REST API interface for the Orion platform.

## Features

- RESTful API endpoints
- Routes requests to internal gRPC services
- Swagger/OpenAPI documentation
- Centralized entry point for clients

## Technologies

- Java 17
- Spring Boot 3.2.0
- Spring Web MVC
- gRPC Client
- SpringDoc OpenAPI

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `ORDER_SERVICE_HOST` | Order service hostname | `localhost` |
| `ORDER_SERVICE_PORT` | Order service gRPC port | `50051` |
| `INVENTORY_SERVICE_HOST` | Inventory service hostname | `localhost` |
| `INVENTORY_SERVICE_PORT` | Inventory service gRPC port | `50052` |
| `ANALYTICS_SERVICE_HOST` | Analytics service hostname | `localhost` |
| `ANALYTICS_SERVICE_PORT` | Analytics service gRPC port | `50053` |

### Ports

- **8080**: HTTP port (REST API)

## REST API Endpoints

### POST /api/orders
Create a new order.

**Request:**
```json
{
  "customerId": "customer-123",
  "items": [
    {
      "sku": "SKU-001",
      "quantity": 2,
      "price": 29.99
    }
  ]
}
```

**Response:**
```json
{
  "orderId": "uuid",
  "status": "CREATED",
  "totalAmount": 59.98,
  "createdAt": 1234567890
}
```

### GET /api/orders/{id}
Retrieve order by ID.

**Response:**
```json
{
  "orderId": "uuid",
  "customerId": "customer-123",
  "status": "CREATED",
  "totalAmount": 59.98,
  "createdAt": 1234567890,
  "items": [
    {
      "sku": "SKU-001",
      "quantity": 2,
      "price": 29.99
    }
  ]
}
```

### GET /api/inventory/{sku}
Get inventory for a SKU.

**Response:**
```json
{
  "sku": "SKU-001",
  "quantity": 98,
  "lastUpdated": 1234567890
}
```

### GET /api/analytics/summary
Get analytics summary.

**Query Parameters:**
- `startDate` (optional): Start date in epoch milliseconds
- `endDate` (optional): End date in epoch milliseconds

**Response:**
```json
{
  "totalOrders": 100,
  "totalRevenue": 5999.00,
  "averageOrderValue": 59.99,
  "totalItemsSold": 250
}
```

## Swagger Documentation

Access the interactive API documentation at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## Running Locally

### With Docker
```bash
docker build -f api-gateway-service/Dockerfile -t api-gateway-service .
docker run -p 8080:8080 \
  -e ORDER_SERVICE_HOST=host.docker.internal \
  api-gateway-service
```

### With Maven
```bash
mvn -f api-gateway-service/pom.xml spring-boot:run
```

## Testing

```bash
# Run unit tests
mvn -f api-gateway-service/pom.xml test

# Test with curl
curl http://localhost:8080/api/inventory/SKU-001
```

## Architecture

The API Gateway acts as a facade, translating REST requests to gRPC calls:

```
REST Client → API Gateway → gRPC Client → Internal Services
```

Benefits:
- Protocol translation (REST ↔ gRPC)
- Single entry point for external clients
- Simplified client integration
- Centralized authentication/authorization (future enhancement)
