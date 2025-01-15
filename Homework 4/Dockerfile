
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
# Use the official OpenJDK image as the base image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the application JAR/WAR file into the container
COPY target/tech_prototype-0.0.1-SNAPSHOT.war /app/my-app.war

# Expose the port that the application runs on
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "/app/my-app.war"]
