FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent pom and shared-protos first
COPY pom.xml ./
COPY shared-protos ./shared-protos

# Build shared-protos
RUN mvn -f shared-protos/pom.xml clean install -DskipTests

# Copy analytics-service and build
COPY analytics-service ./analytics-service
RUN mvn -f analytics-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/analytics-service/target/analytics-service-*.jar app.jar

EXPOSE 8083 50053
ENTRYPOINT ["java", "-jar", "app.jar"]
