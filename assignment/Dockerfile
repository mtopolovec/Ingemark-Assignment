#Build
FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

#Run
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

#ENV SECRET=$SECRET
COPY --from=build /app/target/*.jar ./app.jar
CMD ["java", "-jar", "app.jar"]