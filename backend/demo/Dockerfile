# Backend Dockerfile

# Step 1: Use a base image for Java 17
FROM eclipse-temurin:17-jdk-alpine as build

# Step 2: Set working directory in the container
WORKDIR /app

# Step 3: Copy Gradle wrapper and build scripts
COPY gradlew ./
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Step 4: Download dependencies (caching layer)
RUN ./gradlew --no-daemon dependencies

# Step 5: Copy application source code and build the jar
COPY src src
RUN ./gradlew --no-daemon bootJar

# Step 6: Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
 
