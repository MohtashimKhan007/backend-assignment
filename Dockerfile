# Build stage using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
WORKDIR /app/backend-assignment
RUN mvn clean package -DskipTests

# Run stage using Java 17
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/backend-assignment/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]