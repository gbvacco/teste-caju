FROM openjdk:17-jdk-slim

WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=docker
COPY target/teste-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
