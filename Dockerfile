# Stage 1: Build the JAR
FROM maven:3.9.8-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


# Stage 2: Run the JAR
FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 4001
ENTRYPOINT ["java", "-jar", "app.jar"]