FROM openjdk:21-slim

WORKDIR /app
COPY /build/libs/ .

ENTRYPOINT ["java", "-jar", "gateway-service.jar"]