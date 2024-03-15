FROM openjdk:21-slim

WORKDIR /app
COPY /build/libs/ .

ENTRYPOINT ["java", "-jar", "config-service.jar", "--spring.profiles.active=default"]