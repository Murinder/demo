FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -pl shared-lib,analytics-service -am -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/analytics-service/target/*.jar app.jar
EXPOSE 8005
ENTRYPOINT ["java", "-Xmx256m", "-Xms128m", "-jar", "app.jar"]
