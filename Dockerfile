# Use Maven image with JDK 17 for building the application
FROM maven:3.9.5-eclipse-temurin-17 AS build

# Set working directory inside the container
WORKDIR /app

# Copy Maven configuration files first (for better layer caching)
# Docker caches layers, so if pom.xml doesn't change, dependencies won't be re-downloaded
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml hasn't changed)
RUN mvn dependency:go-offline -B

# Copy the entire project source code
COPY src ./src

# Build the application
# -DskipTests: Skip running tests during build (faster)
# clean: Remove previous builds
# package: Create JAR file
RUN mvn clean package -DskipTests

# ============================================================================
# STAGE 2: Runtime Stage
# ============================================================================
# Use smaller JRE-only image for running the application (reduces image size)
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy the JAR file from build stage
# --from=build: Copy from the "build" stage
# target/*.jar: The JAR file created by Maven
# app.jar: Rename to a simpler name
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 (the port our Spring Boot app runs on)
EXPOSE 8080

# Set JVM options for production
# -Xms512m: Initial heap size
# -Xmx512m: Maximum heap size
# -XX:+UseContainerSupport: JVM recognizes container memory limits
ENV JAVA_OPTS="-Xms512m -Xmx512m -XX:+UseContainerSupport"

# Health check - Docker will check if the app is running
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
# Using exec form (preferred for proper signal handling)
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]


