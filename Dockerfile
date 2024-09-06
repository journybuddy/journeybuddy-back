FROM bellsoft/liberica-openjdk-alpine:18
# or
# FROM openjdk:8-jdk-alpine
# FROM openjdk:11-jdk-alpine

# Copy the Gradle wrapper and build script
COPY gradlew ./
COPY gradle /app/gradle
RUN chmod +x gradlew


# Run Gradle to build the application
RUN ./gradlew clean build


CMD ["./gradlew", "clean", "build"]
# or Maven
# CMD ["./mvnw", "clean", "package"]

VOLUME /tmp

ARG JAR_FILE=build/libs/*.jar
# or Maven
# ARG JAR_FILE_PATH=target/*.jar

COPY ${JAR_FILE} app.jar


EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]
