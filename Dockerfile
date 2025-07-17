FROM openjdk:17-jdk-slim
WORKDIR /app
COPY app.jar/build/libs/busan-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]