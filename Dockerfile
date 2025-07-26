# server base image - java 17
FROM eclipse-temurin:17-jdk

# copy .jar file to docker
COPY ./build/libs/busan-0.0.1-SNAPSHOT.jar app.jar

# always do command
ENTRYPOINT ["java", "-jar", "app.jar"]
