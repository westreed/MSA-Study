FROM openjdk:21-slim

WORKDIR /app
COPY /build/libs/ .

ENTRYPOINT ["java", "-jar", "user-service.jar", "--spring.profiles.active=dev"]