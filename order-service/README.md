# Order Service

The Order Service is responsible for managing customer orders in the Orion platform.

## Features

- Create new orders
- Retrieve order details
- Store order data in PostgreSQL
- Communicate with Inventory Service to adjust stock
- Publish order events to Analytics Service

## Technologies

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- gRPC
- PostgreSQL
- Flyway (database migrations)

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_URL` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/orderdb` |
| `DATABASE_USER` | Database username | `postgres` |
| `DATABASE_PASSWORD` | Database password | `postgres` |
| `INVENTORY_SERVICE_HOST` | Inventory service hostname | `localhost` |
| `INVENTORY_SERVICE_PORT` | Inventory service gRPC port | `50052` |
| `ANALYTICS_SERVICE_HOST` | Analytics service hostname | `localhost` |
| `ANALYTICS_SERVICE_PORT` | Analytics service gRPC port | `50053` |

### Ports

- **8081**: HTTP port (Spring Boot actuator, metrics)
- **50051**: gRPC port

## Database Schema

### orders table
```sql
CREATE TABLE orders (
    order_id VARCHAR(255) PRIMARY KEY,
    customer_id VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

### order_items table
```sql
CREATE TABLE order_items (
    item_id VARCHAR(255) PRIMARY KEY,
    order_id VARCHAR(255) NOT NULL,
    sku VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);
```

## gRPC API

### CreateOrder
Creates a new order and adjusts inventory.

**Request:**
```protobuf
message CreateOrderRequest {
  string customer_id = 1;
  repeated OrderItemRequest items = 2;
}
```

**Response:**
```protobuf
message CreateOrderResponse {
  string order_id = 1;
  string status = 2;
  double total_amount = 3;
  int64 created_at = 4;
}
```

### GetOrder
Retrieves order details by ID.

**Request:**
```protobuf
message GetOrderRequest {
  string order_id = 1;
}
```

**Response:**
```protobuf
message GetOrderResponse {
  string order_id = 1;
  string customer_id = 2;
  string status = 3;
  double total_amount = 4;
  int64 created_at = 5;
  repeated OrderItem items = 6;
}
```

## Running Locally

### With Docker
```bash
docker build -f order-service/Dockerfile -t order-service .
docker run -p 8081:8081 -p 50051:50051 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/orderdb \
  order-service
```

### With Maven
```bash
mvn -f order-service/pom.xml spring-boot:run
```

## Testing

```bash
# Run unit tests
mvn -f order-service/pom.xml test

# Run integration tests (requires Docker)
mvn -f order-service/pom.xml verify
```

## Architecture

```
┌─────────────┐
│   gRPC      │
│  Service    │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  Business   │
│  Service    │
└──────┬──────┘
       │
       ▼
┌─────────────┐    ┌──────────────┐
│ Repository  │───▶│  PostgreSQL  │
└─────────────┘    └──────────────┘
```

The service follows a layered architecture:
- **gRPC Layer**: Handles gRPC requests/responses
- **Service Layer**: Contains business logic
- **Repository Layer**: Data access with Spring Data JPA
- **Entity Layer**: JPA entities
