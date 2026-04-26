# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copy the maven wrapper and pom file first to cache dependencies
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

# Copy the source code and build
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]