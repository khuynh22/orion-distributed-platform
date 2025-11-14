FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent pom and shared-protos first
COPY pom.xml ./
COPY shared-protos ./shared-protos

# Build shared-protos
RUN mvn -f shared-protos/pom.xml clean install -DskipTests

# Copy order-service and build
COPY order-service ./order-service
RUN mvn -f order-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/order-service/target/order-service-*.jar app.jar

EXPOSE 8081 50051
ENTRYPOINT ["java", "-jar", "app.jar"]
