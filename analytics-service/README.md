# Analytics Service

The Analytics Service aggregates and analyzes order events in the Orion platform.

## Features

- Record order events
- Generate analytics summaries
- Store analytics data in MongoDB
- Provide insights via gRPC

## Technologies

- Java 17
- Spring Boot 3.2.0
- Spring Data MongoDB
- gRPC
- MongoDB

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `MONGODB_URI` | MongoDB connection URI | `mongodb://localhost:27017/analytics` |

### Ports

- **8083**: HTTP port (Spring Boot actuator, metrics)
- **50053**: gRPC port

## Data Model

### order_analytics collection
```json
{
  "_id": "ObjectId",
  "orderId": "string",
  "customerId": "string",
  "totalAmount": "double",
  "itemCount": "int",
  "timestamp": "long",
  "recordedAt": "ISODate"
}
```

## gRPC API

### RecordOrderEvent
Records an order event for analytics.

**Request:**
```protobuf
message RecordOrderEventRequest {
  string order_id = 1;
  string customer_id = 2;
  double total_amount = 3;
  int32 item_count = 4;
  int64 timestamp = 5;
}
```

**Response:**
```protobuf
message RecordOrderEventResponse {
  bool success = 1;
  string message = 2;
}
```

### GetOrderSummary
Retrieves analytics summary for a date range.

**Request:**
```protobuf
message GetOrderSummaryRequest {
  int64 start_date = 1;
  int64 end_date = 2;
}
```

**Response:**
```protobuf
message GetOrderSummaryResponse {
  int32 total_orders = 1;
  double total_revenue = 2;
  double average_order_value = 3;
  int32 total_items_sold = 4;
}
```

## Running Locally

### With Docker
```bash
docker build -f analytics-service/Dockerfile -t analytics-service .
docker run -p 8083:8083 -p 50053:50053 \
  -e MONGODB_URI=mongodb://host.docker.internal:27017/analytics \
  analytics-service
```

### With Maven
```bash
mvn -f analytics-service/pom.xml spring-boot:run
```

## Testing

```bash
# Run unit tests
mvn -f analytics-service/pom.xml test

# Run integration tests (requires Docker)
mvn -f analytics-service/pom.xml verify
```

## Analytics Queries

The service provides insights such as:
- Total number of orders
- Total revenue
- Average order value
- Total items sold

These metrics can be filtered by date range.
