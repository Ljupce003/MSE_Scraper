# Stage 1: Build Java Spring application
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml ./
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Prepare Python environment
FROM python:3.11.9-slim AS python-env
WORKDIR /python-app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Stage 3: Final image
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Install Python runtime and dependencies
RUN apt-get update && apt-get install -y \
    python3.11 \
    python3.11-distutils \
    python3-pip \
    libpython3.11 \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# Copy the built Java application
COPY --from=builder /app/target/*.jar app.jar

# Copy Python dependencies (installed via pip in python-env stage)
COPY --from=python-env /usr/local /usr/local

COPY src ./src

EXPOSE 8093

# Set the default command to run your Java application
ENTRYPOINT ["java", "-jar", "app.jar"]
