# ===========================
# 🧱 1. Build Stage
# ===========================
# Use Maven with Temurin JDK 25 for building
FROM maven:3.9.9-eclipse-temurin-25 AS builder

# Set working directory
WORKDIR /app

# Copy Maven wrapper and configuration for caching dependencies
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies to cache Maven layers
RUN ./mvnw dependency:go-offline -B

# Copy project source
COPY src ./src

# Build the Spring Boot JAR (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# ===========================
# ☕ 2. Runtime Stage
# ===========================
# Use lightweight Temurin JDK 25 runtime
FROM eclipse-temurin:25-jdk-jammy

# Set working directory
WORKDIR /app

# Copy the built JAR file from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose Spring Boot default port
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
