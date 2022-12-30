# Build stage
FROM maven:3.8.2-jdk-11-slim AS build
COPY /src /src
COPY /pom.xml .
RUN mvn clean install

# Package stage
FROM openjdk:11-jre-slim
COPY --from=build /target/car-rest-service-0.0.2.jar /usr/local/lib/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar","/usr/local/lib/app.jar"]