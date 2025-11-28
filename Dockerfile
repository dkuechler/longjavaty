FROM eclipse-temurin:21

WORKDIR /app

# Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x ./mvnw

RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

EXPOSE 8080
CMD ["java", "-jar", "target/longjavaty-0.0.1-SNAPSHOT.jar"]