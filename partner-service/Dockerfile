FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -pl shared-lib,partner-service -am -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/partner-service/target/*.jar app.jar
EXPOSE 8007
ENTRYPOINT ["java", "-Xmx256m", "-Xms128m", "-jar", "app.jar"]
