## Base image with Python and Java installed
#FROM openjdk:17-jdk-slim
#
## Install Python 3 and pip, and create a symlink for `python`
#RUN apt-get update && \
#    apt-get install -y python3 python3-pip && \
#    ln -s /usr/bin/python3 /usr/bin/python && \
#    apt-get clean
#
## Set the working directory
#WORKDIR /src
#
## Copy the Spring Boot application JAR file
#COPY target/tech_m_service-0.0.1-SNAPSHOT.jar /src/tech_m_service-0.0.1-SNAPSHOT.jar
#
## Copy Python resources
#COPY src/main/resources/python /src/main/resources/python
#
## Install Python dependencies (if you have any)
#RUN pip3 install --no-cache-dir -r /src/main/resources/python/requirements.txt
#
## Expose the application port
#EXPOSE 8091
#
## Command to run the Spring Boot application in the foreground and Python script in the background
#CMD java -jar /src/tech_m_service-0.0.1-SNAPSHOT.jar & \
#    python /src/main/resources/python/Main.py && \
#    wait $!


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

EXPOSE 8091

# Set the default command to run your Java application
ENTRYPOINT ["java", "-jar", "app.jar"]

