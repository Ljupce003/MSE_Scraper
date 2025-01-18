# Base image with Python and Java installed
FROM openjdk:17-jdk-slim

# Install Python 3 and pip, and create a symlink for `python`
RUN apt-get update && \
    apt-get install -y python3 python3-pip && \
    ln -s /usr/bin/python3 /usr/bin/python && \
    apt-get clean

# Set the working directory
WORKDIR /src

# Copy the Spring Boot application JAR file
COPY target/tech_m_service-0.0.1-SNAPSHOT.jar /src/tech_m_service-0.0.1-SNAPSHOT.jar

# Copy Python resources
COPY src/main/resources/python /src/main/resources/python

# Install Python dependencies (if you have any)
RUN pip3 install --no-cache-dir -r /src/main/resources/python/requirements.txt

# Expose the application port
EXPOSE 8091

# Command to run the Spring Boot application in the foreground and Python script in the background
CMD java -jar /src/tech_m_service-0.0.1-SNAPSHOT.jar & \
    python /src/main/resources/python/Main.py && \
    wait $!
