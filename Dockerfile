# Stage 1: Copy pre-built JAR (no build inside container)
FROM eclipse-temurin:21-jre-alpine AS package-stage
WORKDIR /app
COPY target/*.jar app.jar

# Stage 2: Final runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=package-stage /app/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
