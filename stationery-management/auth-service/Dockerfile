FROM eclipse-temurin:17-jre-jammy
VOLUME /tmp
COPY target/auth-service-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]