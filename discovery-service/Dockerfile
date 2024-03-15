FROM openjdk:21-slim

WORKDIR /app
COPY /build/libs/ .

ENTRYPOINT ["java", "-jar", "discovery-service.jar"]