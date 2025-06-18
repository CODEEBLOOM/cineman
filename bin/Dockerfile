FROM openjdk:17

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} cineman.jar

ENTRYPOINT ["java", "-jar", "cineman.jar"]

EXPOSE 8080