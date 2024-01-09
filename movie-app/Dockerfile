# Use an official OpenJDK runtime as a parent image
FROM --platform=amd64 openjdk:17.0.2-oraclelinux8

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file built by your Spring Boot application into the container at the working directory
COPY target/app.jar app.jar

COPY ./image_uploads ./image_uploads
COPY ./video_uploads ./video_uploads

# Expose the port your application will run on
EXPOSE 8080

# Specify the command to run your application when the container starts
CMD ["java", "-jar", "app.jar"]