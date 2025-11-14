FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent pom and shared-protos first
COPY pom.xml ./
COPY shared-protos ./shared-protos

# Build shared-protos
RUN mvn -f shared-protos/pom.xml clean install -DskipTests

# Copy api-gateway-service and build
COPY api-gateway-service ./api-gateway-service
RUN mvn -f api-gateway-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/api-gateway-service/target/api-gateway-service-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
