# Orion Platform - Architecture Documentation

## Overview

The Orion Distributed Platform is a microservices-based system demonstrating modern distributed system patterns with gRPC, REST APIs, and event-driven architecture.

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     External Clients                         │
│              (Web, Mobile, Third-party APIs)                 │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP/REST
                         │
┌────────────────────────▼────────────────────────────────────┐
│                   API Gateway Service                        │
│  - REST API Endpoints                                        │
│  - Request Validation                                        │
│  - Protocol Translation (REST → gRPC)                        │
│  - API Documentation (Swagger)                               │
│  Port: 8080 (HTTP)                                           │
└──────────────────┬──────────────┬──────────────┬────────────┘
                   │              │              │
                   │ gRPC         │ gRPC         │ gRPC
                   │              │              │
        ┌──────────▼──────┐  ┌────▼────┐  ┌─────▼──────┐
        │  Order Service  │  │Inventory│  │ Analytics  │
        │                 │  │ Service │  │  Service   │
        │ - Create Orders │  │- Track  │  │- Aggregate │
        │ - Get Orders    │  │  Stock  │  │  Events    │
        │ - Order Logic   │  │- Adjust │  │- Generate  │
        │                 │  │  Levels │  │  Reports   │
        │ Port: 50051     │  │Port:    │  │Port: 50053 │
        │      (gRPC)     │  │ 50052   │  │    (gRPC)  │
        └────────┬────────┘  └────┬────┘  └─────┬──────┘
                 │                │              │
                 │                │              │
        ┌────────▼────────┐  ┌────▼────┐  ┌─────▼──────┐
        │   PostgreSQL    │  │Postgres │  │  MongoDB   │
        │    (Orders)     │  │(Invntry)│  │(Analytics) │
        │                 │  │         │  │            │
        │ - orders        │  │- invntry│  │- order_    │
        │ - order_items   │  │         │  │  analytics │
        └─────────────────┘  └─────────┘  └────────────┘
```

## Service Communication Patterns

### External Communication
- **Protocol**: REST/HTTP
- **Format**: JSON
- **Port**: 8080
- **Use Case**: Client-facing API

### Internal Communication
- **Protocol**: gRPC
- **Format**: Protocol Buffers
- **Ports**: 50051-50053
- **Benefits**:
  - Type-safe contracts
  - High performance
  - Code generation
  - Language agnostic

## Service Responsibilities

### API Gateway Service

**Purpose**: Single entry point for external clients

**Responsibilities**:
- Accept REST API requests
- Validate input
- Route to appropriate internal services
- Translate REST ↔ gRPC
- Aggregate responses
- Handle errors
- Provide API documentation

**Technology Stack**:
- Spring Boot
- Spring Web MVC
- SpringDoc OpenAPI
- gRPC Client

**External Dependencies**:
- Order Service (gRPC)
- Inventory Service (gRPC)
- Analytics Service (gRPC)

### Order Service

**Purpose**: Manage customer orders

**Responsibilities**:
- Create new orders
- Retrieve order details
- Calculate order totals
- Coordinate with Inventory for stock adjustments
- Publish events to Analytics

**Technology Stack**:
- Spring Boot
- Spring Data JPA
- gRPC Server
- PostgreSQL
- Flyway (migrations)

**Database Schema**:
```sql
orders (
  order_id VARCHAR PK,
  customer_id VARCHAR,
  status VARCHAR,
  total_amount DECIMAL,
  created_at TIMESTAMP
)

order_items (
  item_id VARCHAR PK,
  order_id VARCHAR FK,
  sku VARCHAR,
  quantity INTEGER,
  price DECIMAL
)
```

**External Dependencies**:
- Inventory Service (stock adjustment)
- Analytics Service (event publishing)

### Inventory Service

**Purpose**: Track product inventory levels

**Responsibilities**:
- Maintain inventory counts
- Adjust inventory (increment/decrement)
- Provide current stock levels
- Handle inventory queries

**Technology Stack**:
- Spring Boot
- Spring Data JPA
- gRPC Server
- PostgreSQL
- Flyway (migrations)

**Database Schema**:
```sql
inventory (
  sku VARCHAR PK,
  quantity INTEGER,
  last_updated TIMESTAMP
)
```

**External Dependencies**: None

### Analytics Service

**Purpose**: Aggregate and analyze order data

**Responsibilities**:
- Record order events
- Calculate statistics
- Generate reports
- Provide analytics summaries

**Technology Stack**:
- Spring Boot
- Spring Data MongoDB
- gRPC Server
- MongoDB

**Data Model**:
```javascript
order_analytics {
  _id: ObjectId,
  orderId: String,
  customerId: String,
  totalAmount: Double,
  itemCount: Integer,
  timestamp: Long,
  recordedAt: ISODate
}
```

**External Dependencies**: None

## Data Flow Examples

### Create Order Flow

```
1. Client → API Gateway
   POST /api/orders
   {
     "customerId": "cust-123",
     "items": [{"sku": "SKU-001", "quantity": 2, "price": 29.99}]
   }

2. API Gateway → Order Service (gRPC)
   CreateOrder(CreateOrderRequest)

3. Order Service → Inventory Service (gRPC)
   AdjustInventory(sku: "SKU-001", quantity: -2)

4. Inventory Service → PostgreSQL
   UPDATE inventory SET quantity = quantity - 2

5. Order Service → PostgreSQL
   INSERT INTO orders, order_items

6. Order Service → Analytics Service (gRPC)
   RecordOrderEvent(order details)

7. Analytics Service → MongoDB
   INSERT INTO order_analytics

8. Response flows back through the chain
   Analytics → Order → API Gateway → Client
```

### Get Inventory Flow

```
1. Client → API Gateway
   GET /api/inventory/SKU-001

2. API Gateway → Inventory Service (gRPC)
   GetInventory(sku: "SKU-001")

3. Inventory Service → PostgreSQL
   SELECT * FROM inventory WHERE sku = 'SKU-001'

4. Response flows back
   Inventory → API Gateway → Client
```

## Design Patterns

### Microservices Pattern
- Each service is independently deployable
- Services own their data
- Communication via well-defined APIs

### API Gateway Pattern
- Single entry point
- Protocol translation
- Request aggregation

### Database per Service
- Each service has its own database
- Data isolation
- Independent scaling

### Service Registry (Future)
- Service discovery
- Load balancing
- Health checks

### Circuit Breaker (Future)
- Fault tolerance
- Graceful degradation
- Retry logic

## Scalability Considerations

### Horizontal Scaling
- All services are stateless
- Can run multiple instances
- Load balanced via Kubernetes

### Database Scaling
- PostgreSQL: Read replicas
- MongoDB: Sharding
- Connection pooling

### Caching (Future Enhancement)
- Redis for frequently accessed data
- Cache inventory queries
- Cache analytics reports

## Security Considerations

### Current State
- Internal network only
- No authentication/authorization
- Plain HTTP/gRPC

### Future Enhancements
- JWT authentication
- OAuth2/OIDC
- TLS/SSL encryption
- API rate limiting
- Service-to-service authentication

## Deployment Architecture

### Docker Compose (Development)
```
┌─────────────────────────────────────┐
│         Docker Network              │
│                                     │
│  ┌──────────┐  ┌──────────┐       │
│  │Postgres  │  │Postgres  │       │
│  │ (Order)  │  │ (Invntry)│       │
│  └──────────┘  └──────────┘       │
│                                     │
│  ┌──────────┐                      │
│  │ MongoDB  │                      │
│  └──────────┘                      │
│                                     │
│  ┌──────────┐  ┌──────────┐       │
│  │  Order   │  │Inventory │       │
│  │ Service  │  │ Service  │       │
│  └──────────┘  └──────────┘       │
│                                     │
│  ┌──────────┐  ┌──────────┐       │
│  │Analytics │  │   API    │       │
│  │ Service  │  │ Gateway  │       │
│  └──────────┘  └──────────┘       │
│                      │              │
└──────────────────────┼──────────────┘
                       │
                   Port 8080
```

### Kubernetes (Production)
```
┌──────────────────────────────────────────┐
│         Kubernetes Cluster               │
│                                          │
│  Namespace: orion-platform               │
│                                          │
│  ┌────────────────────────────────┐     │
│  │      StatefulSets              │     │
│  │  - postgres-order (PVC)        │     │
│  │  - postgres-inventory (PVC)    │     │
│  │  - mongodb (PVC)               │     │
│  └────────────────────────────────┘     │
│                                          │
│  ┌────────────────────────────────┐     │
│  │      Deployments               │     │
│  │  - order-service (replicas)    │     │
│  │  - inventory-service (replicas)│     │
│  │  - analytics-service (replicas)│     │
│  │  - api-gateway (replicas)      │     │
│  └────────────────────────────────┘     │
│                                          │
│  ┌────────────────────────────────┐     │
│  │      Services                  │     │
│  │  - ClusterIP for internal      │     │
│  │  - NodePort for API Gateway    │     │
│  └────────────────────────────────┘     │
│                                          │
│  ┌────────────────────────────────┐     │
│  │      ConfigMaps & Secrets      │     │
│  │  - Environment configuration   │     │
│  │  - Database credentials        │     │
│  └────────────────────────────────┘     │
│                                          │
│  ┌────────────────────────────────┐     │
│  │         Ingress                │     │
│  │  - External access routing     │     │
│  └────────────────────────────────┘     │
└──────────────────────────────────────────┘
```

## Monitoring & Observability (Future)

### Metrics
- Spring Boot Actuator
- Prometheus
- Grafana dashboards

### Logging
- Centralized logging (ELK stack)
- Structured JSON logs
- Correlation IDs

### Tracing
- Distributed tracing (Jaeger/Zipkin)
- Request flow visualization

### Health Checks
- Liveness probes
- Readiness probes
- Dependency health

## Testing Strategy

### Unit Tests
- JUnit 5
- Mockito for mocking
- Test business logic

### Integration Tests
- Testcontainers
- Real databases
- End-to-end scenarios

### API Tests
- REST Assured
- Postman/Newman

### Load Tests (Future)
- JMeter/Gatling
- Performance benchmarks

## Technology Decisions

### Why gRPC for Internal Communication?
- **Performance**: Binary protocol, HTTP/2
- **Type Safety**: Protocol Buffers
- **Language Agnostic**: Multi-language support
- **Streaming**: Built-in support
- **Code Generation**: Auto-generate clients

### Why REST for External API?
- **Ubiquity**: Universal support
- **Ease of Use**: Simple to understand
- **Tooling**: Excellent debugging tools
- **Documentation**: Swagger/OpenAPI

### Why Multiple Databases?
- **Right Tool**: PostgreSQL for transactions, MongoDB for analytics
- **Polyglot Persistence**: Use best database for each use case
- **Independence**: Services own their data

### Why Kubernetes?
- **Orchestration**: Container management
- **Scaling**: Horizontal pod autoscaling
- **Self-Healing**: Automatic restart
- **Service Discovery**: Built-in
- **Industry Standard**: Wide adoption

## Future Enhancements

1. **Event-Driven Architecture**
   - Kafka/RabbitMQ message broker
   - Async event processing
   - Event sourcing

2. **CQRS Pattern**
   - Separate read/write models
   - Optimized queries
   - Better scalability

3. **API Versioning**
   - Multiple API versions
   - Backward compatibility
   - Deprecation strategy

4. **Advanced Analytics**
   - Real-time dashboards
   - Machine learning insights
   - Predictive analytics

5. **Multi-Region Deployment**
   - Geo-distributed
   - Lower latency
   - High availability

## References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [gRPC Documentation](https://grpc.io/docs/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Docker Documentation](https://docs.docker.com/)
- [Protocol Buffers](https://developers.google.com/protocol-buffers)
