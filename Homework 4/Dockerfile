
# # Use a lightweight Python base image
# FROM python:3.9-slim

# # Set the working directory in the container
# WORKDIR /app

# # Copy project files into the container
# COPY . .

# # Install project dependencies
# RUN pip install -r requirements.txt

# # Expose the port the app runs on
# EXPOSE 5000

# # Command to run the application
# CMD ["python", "app.py"]

# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the application JAR to the container
COPY target/my-app.jar /app/my-app.jar

# Expose the application port
EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "/app/my-app.jar"]
