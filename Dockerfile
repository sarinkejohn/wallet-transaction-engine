
# Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy only pom first for dependency caching
COPY pom.xml .

# Download dependencies (cached layer)
RUN mvn -B dependency:go-offline

# Copy source code
COPY src ./src

# Build application
RUN mvn -B clean package -DskipTests


# Runtime Stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy built JAR explicitly (stable production approach)
COPY --from=builder /app/target/minipayment-engine-0.0.1-SNAPSHOT.jar app.jar

# Expose application port
EXPOSE 8080

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]