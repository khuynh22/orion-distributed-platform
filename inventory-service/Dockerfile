FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent pom and shared-protos first
COPY pom.xml ./
COPY shared-protos ./shared-protos

# Build shared-protos
RUN mvn -f shared-protos/pom.xml clean install -DskipTests

# Copy inventory-service and build
COPY inventory-service ./inventory-service
RUN mvn -f inventory-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/inventory-service/target/inventory-service-*.jar app.jar

EXPOSE 8082 50052
ENTRYPOINT ["java", "-jar", "app.jar"]
