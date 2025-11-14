# Contributing to Orion Distributed Platform

Thank you for your interest in contributing to the Orion Distributed Platform! This document provides guidelines and instructions for contributing.

## Development Setup

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- Docker and Docker Compose
- Git
- IDE (IntelliJ IDEA, Eclipse, or VS Code recommended)

### Getting Started

1. **Fork the repository**
   ```bash
   # Click the "Fork" button on GitHub
   ```

2. **Clone your fork**
   ```bash
   git clone https://github.com/YOUR_USERNAME/orion-distributed-platform.git
   cd orion-distributed-platform
   ```

3. **Build the project**
   ```bash
   ./scripts/build-all.sh
   ```

4. **Run tests**
   ```bash
   mvn test
   ```

5. **Start services locally**
   ```bash
   docker-compose up
   ```

## Project Structure

```
orion-distributed-platform/
├── shared-protos/           # gRPC protocol definitions
├── order-service/           # Order management service
├── inventory-service/       # Inventory tracking service
├── analytics-service/       # Analytics aggregation service
├── api-gateway-service/     # REST API gateway
├── k8s/                     # Kubernetes manifests
├── scripts/                 # Helper scripts
└── docker-compose.yml       # Docker Compose configuration
```

## Coding Standards

### Java Code Style

- Follow standard Java naming conventions
- Use meaningful variable and method names
- Add comments for complex logic
- Keep methods focused and concise
- Use dependency injection

### Code Formatting

- Indentation: 4 spaces
- Line length: 120 characters max
- Braces: K&R style

### Commit Messages

Follow conventional commit format:

```
<type>(<scope>): <subject>

<body>

<footer>
```

Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

Example:
```
feat(order-service): add order cancellation endpoint

Add new REST endpoint to cancel orders. Updates order status
to CANCELLED and restores inventory.

Closes #123
```

## Making Changes

### 1. Create a Branch

```bash
git checkout -b feature/your-feature-name
# or
git checkout -b fix/bug-description
```

### 2. Make Your Changes

- Write clean, maintainable code
- Add tests for new functionality
- Update documentation as needed
- Ensure all tests pass

### 3. Test Your Changes

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify

# Test with Docker Compose
docker-compose up --build

# Test with Kubernetes
./scripts/k8s-deploy.sh
```

### 4. Commit Your Changes

```bash
git add .
git commit -m "feat(service-name): description of changes"
```

### 5. Push to Your Fork

```bash
git push origin feature/your-feature-name
```

### 6. Create a Pull Request

1. Go to the original repository on GitHub
2. Click "New Pull Request"
3. Select your fork and branch
4. Fill in the PR template
5. Submit the pull request

## Pull Request Guidelines

### PR Title

Use conventional commit format:
```
feat(order-service): add order cancellation
```

### PR Description

Include:
- Summary of changes
- Motivation and context
- How to test the changes
- Screenshots (if applicable)
- Related issues

Example:
```markdown
## Description
Adds order cancellation functionality to the order service.

## Changes
- Added `cancelOrder` endpoint
- Updated order entity with CANCELLED status
- Added inventory restoration logic
- Added unit and integration tests

## Testing
1. Start services with docker-compose
2. Create an order: `POST /api/orders`
3. Cancel the order: `POST /api/orders/{id}/cancel`
4. Verify inventory was restored

## Related Issues
Closes #123
```

### PR Checklist

- [ ] Code follows project style guidelines
- [ ] Added/updated tests
- [ ] All tests pass
- [ ] Updated documentation
- [ ] No console warnings or errors
- [ ] Builds successfully with Maven
- [ ] Works with Docker Compose
- [ ] Works with Kubernetes (if applicable)

## Testing Guidelines

### Unit Tests

- Use JUnit 5 for test framework
- Use Mockito for mocking
- Aim for >80% code coverage
- Test edge cases and error conditions

Example:
```java
@Test
void testCreateOrder() {
    // Arrange
    CreateOrderRequest request = // ... setup
    when(repository.save(any())).thenReturn(expectedOrder);
    
    // Act
    Order result = service.createOrder(request);
    
    // Assert
    assertNotNull(result);
    assertEquals(expectedOrder.getId(), result.getId());
    verify(repository, times(1)).save(any());
}
```

### Integration Tests

- Use Testcontainers for databases
- Test complete workflows
- Verify inter-service communication

## Adding a New Feature

### Example: Adding a New Service

1. **Create service structure**
   ```bash
   mkdir -p new-service/src/{main,test}/java
   ```

2. **Add to parent POM**
   ```xml
   <modules>
       <module>new-service</module>
   </modules>
   ```

3. **Create service POM**
   ```xml
   <parent>
       <groupId>com.orion.platform</groupId>
       <artifactId>orion-distributed-platform</artifactId>
       <version>1.0.0-SNAPSHOT</version>
   </parent>
   <artifactId>new-service</artifactId>
   ```

4. **Implement service code**
5. **Add tests**
6. **Create Dockerfile**
7. **Update docker-compose.yml**
8. **Create Kubernetes manifests**
9. **Update documentation**

## Documentation

### Code Documentation

- Add JavaDoc for public APIs
- Document complex algorithms
- Explain non-obvious decisions

### README Updates

- Update main README for major features
- Update service README for service changes
- Keep examples up to date

## Getting Help

- Open an issue for bugs or feature requests
- Join discussions on GitHub Discussions
- Check existing issues and PRs
- Read the documentation

## Code Review Process

1. Automated checks run on PR
2. Maintainer reviews code
3. Address feedback
4. Approval and merge

## Release Process

1. Update version numbers
2. Update CHANGELOG.md
3. Create release tag
4. Build and push Docker images
5. Update documentation

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

## Questions?

Feel free to open an issue or reach out to the maintainers!

Thank you for contributing to Orion! 🚀
