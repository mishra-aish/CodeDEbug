# ===========================
# 🧱 1. Build Stage
# ===========================
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy Maven wrapper & config
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Make wrapper executable
RUN chmod +x mvnw

# Cache dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build jar
RUN ./mvnw clean package -DskipTests


# ===========================
# ☕ 2. Runtime Stage
# ===========================
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copy jar from builder
COPY --from=builder /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run app
ENTRYPOINT ["java", "-jar", "app.jar"]