# Use Java 23 runtime
FROM eclipse-temurin:23-jre

# Working directory inside container
WORKDIR /app

# Copy the generated JAR (any name works)
COPY build/libs/*.jar app.jar

# Expose default Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","app.jar"]
