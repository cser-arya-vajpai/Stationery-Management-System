# Use a base image containing Java 17 Runtime on Ubuntu Jammy.
FROM eclipse-temurin:17-jre-jammy

# Create a temporary storage directory inside the container.
VOLUME /tmp

# Copy the built Eureka Server JAR from the target folder into the container as app.jar.
COPY target/eureka-server-1.0.0.jar app.jar

# Automatically start the Eureka Server application when the container runs.
ENTRYPOINT ["java", "-jar", "/app.jar"]