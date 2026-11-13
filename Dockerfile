# Build stage using Maven and Eclipse Temurin JDK 17
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependencies layer
COPY backend-assignment/pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source code and build package
COPY backend-assignment/src ./src
RUN mvn clean package -DskipTests

# Runtime stage using Eclipse Temurin JRE 17
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]