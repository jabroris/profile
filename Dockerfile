# ===== Stage 1: Build =====
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy Gradle wrapper and config first (layer cache: dependencies change less often than code)
COPY gradlew gradlew.bat gradle.properties settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle/

# Download dependencies (cached layer)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon -q || true

# Copy source and build
COPY src ./src/
RUN ./gradlew bootJar --no-daemon -x test

# ===== Stage 2: Runtime =====
FROM eclipse-temurin:17-jre-alpine

LABEL org.opencontainers.image.title="Ivaylo Blagoev Portfolio"
LABEL org.opencontainers.image.description="Spring Boot / Kotlin portfolio application"
LABEL org.opencontainers.image.source="https://github.com/jabroris/portfolio"

# Non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+ExitOnOutOfMemoryError", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "/app/app.jar"]
