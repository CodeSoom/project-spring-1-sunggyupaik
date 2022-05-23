FROM openjdk:11.0.1
ARG JAR_FILE=app/build/libs/\*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod", "/app.jar"]
