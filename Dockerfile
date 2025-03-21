# Stage 1: Build the JAR
FROM maven:3.9.8-eclipse-temurin-17 AS builder
WORKDIR /app
# Copy POM first to cache dependencies
COPY pom.xml .
# Pre-download dependencies
# Copy source code (including Application.java)
COPY src ./src
RUN mvn clean package -DskipTests
# Build the JAR

# Stage 2: Run the JAR
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
COPY src/main/resources/static/ /app/static/
EXPOSE 4001
ENV KAFKA_BOOTSTRAP_SERVERS=kafka:9093
ENTRYPOINT ["java", "-jar", "app.jar"]