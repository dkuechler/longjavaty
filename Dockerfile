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

# Setup non-root user
RUN addgroup --system --gid 1001 spring && \
    adduser --system --uid 1001 --ingroup spring --shell /bin/false spring

COPY --from=builder /app/target/*.jar app.jar
RUN chown spring:spring app.jar

USER spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]