FROM openjdk:17
WORKDIR /app
COPY ./jar/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]