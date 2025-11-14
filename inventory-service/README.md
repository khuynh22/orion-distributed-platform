# Inventory Service

The Inventory Service manages product inventory levels in the Orion platform.

## Features

- Track product inventory quantities
- Adjust inventory levels
- Provide inventory information to other services
- Store data in PostgreSQL

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
| `DATABASE_URL` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/inventorydb` |
| `DATABASE_USER` | Database username | `postgres` |
| `DATABASE_PASSWORD` | Database password | `postgres` |

### Ports

- **8082**: HTTP port (Spring Boot actuator, metrics)
- **50052**: gRPC port

## Database Schema

### inventory table
```sql
CREATE TABLE inventory (
    sku VARCHAR(255) PRIMARY KEY,
    quantity INTEGER NOT NULL DEFAULT 0,
    last_updated TIMESTAMP NOT NULL
);
```

## gRPC API

### AdjustInventory
Adjusts inventory quantity for a SKU.

**Request:**
```protobuf
message AdjustInventoryRequest {
  string sku = 1;
  int32 quantity_change = 2;
  string reason = 3;
}
```

**Response:**
```protobuf
message AdjustInventoryResponse {
  string sku = 1;
  int32 current_quantity = 2;
  bool success = 3;
  string message = 4;
}
```

### GetInventory
Retrieves inventory information for a SKU.

**Request:**
```protobuf
message GetInventoryRequest {
  string sku = 1;
}
```

**Response:**
```protobuf
message GetInventoryResponse {
  string sku = 1;
  int32 quantity = 2;
  int64 last_updated = 3;
}
```

## Running Locally

### With Docker
```bash
docker build -f inventory-service/Dockerfile -t inventory-service .
docker run -p 8082:8082 -p 50052:50052 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/inventorydb \
  inventory-service
```

### With Maven
```bash
mvn -f inventory-service/pom.xml spring-boot:run
```

## Testing

```bash
# Run unit tests
mvn -f inventory-service/pom.xml test

# Run integration tests (requires Docker)
mvn -f inventory-service/pom.xml verify
```

## Sample Data

The service comes with pre-populated sample inventory:

| SKU | Initial Quantity |
|-----|------------------|
| SKU-001 | 100 |
| SKU-002 | 200 |
| SKU-003 | 150 |
| SKU-004 | 75 |
| SKU-005 | 300 |
