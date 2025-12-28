# Build stage
FROM eclipse-temurin:21 AS builder
WORKDIR /app

# Dependency caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x ./mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Build application
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Setup non-root user (using standard useradd/groupadd for portability)
# We ensure valid shell is /bin/false and home dir is /app
RUN groupadd -r spring && useradd -r -g spring -d /app -s /bin/false spring

# Create writable directory for logs/temp if needed
RUN mkdir -p /app/logs && chown -R spring:spring /app

# Copy the deterministic build artifact
COPY --from=builder --chown=spring:spring /app/target/longjavaty.jar app.jar

USER spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]