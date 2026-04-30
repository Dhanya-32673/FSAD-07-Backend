# ── Stage 1: Build ──────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependencies first (speeds up rebuilds)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build the fat JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# ── Stage 2: Run ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Install bash/sed for the startup script
RUN apk add --no-cache bash sed

COPY --from=build /app/target/election-monitor-1.0.0.jar app.jar
COPY start.sh start.sh
RUN chmod +x start.sh

EXPOSE 8080

ENTRYPOINT ["sh", "start.sh"]

