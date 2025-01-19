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

#d

# Install system dependencies for Playwright
RUN apt-get update && apt-get install -y \
    libgtk-3-0 \
    libdbus-1-3 \
    libgdk-pixbuf2.0-0 \
    libasound2 \
    libx11-xcb1 \
    libnss3 \
    libxtst6 \
    libxss1 \
    xdg-utils \
    curl \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Install Node.js (necessary for Playwright)
RUN curl -sL https://deb.nodesource.com/setup_18.x | bash - \
    && apt-get install -y nodejs \
    && npm install -g npm

# Install Playwright and its browsers
RUN npm install playwright
RUN npx playwright install

#d

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

EXPOSE 8092

# Set the default command to run your Java application
ENTRYPOINT ["java", "-jar", "app.jar"]
