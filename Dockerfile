# ===========================
# 🧱 1. Build Stage
# ===========================
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Set working directory
WORKDIR /app

# Copy Maven descriptor files first (for caching dependencies)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

# Download dependencies (improves caching)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (creates JAR in target/)
RUN ./mvnw clean package -DskipTests

# ===========================
# ☕ 2. Runtime Stage
# ===========================
FROM eclipse-temurin:21-jdk-jammy

# Set working directory inside container
WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the application port (default: 8080)
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
