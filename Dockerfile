# Stage 1: Build the frontend
FROM node:18-alpine AS frontend-build

# Set the working directory
WORKDIR /app/frontend

# Copy the frontend project files
COPY sprinttracker_frontend/package*.json ./
COPY sprinttracker_frontend/ ./

# Install dependencies and build the frontend
RUN npm install
RUN npm run build

# Stage 2: Build the backend
FROM jfrog.proxy.devinfra.ptec/docker-ims-dev/ims_reporting_openjdk:21-minimal-minideb-latest AS backend-build

# Install Maven
RUN apt-get update && apt-get install -y maven

# Set the working directory
WORKDIR /app/backend

# Copy the parent POM
COPY pom.xml /app/pom.xml

# Copy the backend project files
COPY sprinttracker/pom.xml /app/backend/pom.xml
COPY sprinttracker/src /app/backend/src

# Package the backend application
RUN mvn -f /app/backend/pom.xml clean package -DskipTests

# Stage 3: Create the final image
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the backend jar file
COPY --from=backend-build /app/backend/target/sprinttracker-0.0.1-SNAPSHOT-jar-with-dependencies.jar ./sprinttracker.jar

# Copy the frontend build files
COPY --from=frontend-build /app/frontend/dist /app/frontend/
COPY --from=frontend-build /app/frontend/dist /app/backend/src/main/resources/static/

# Expose the port the application runs on
EXPOSE 8000

# Run the backend application
CMD ["java", "-jar", "sprinttracker.jar"]