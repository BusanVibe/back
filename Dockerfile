FROM openjdk:17

# 컨테이너 내 작업 디렉토리 설정
WORKDIR /home/ubuntu/app/app.jar/build/libs

# 호스트의 빌드된 jar를 컨테이너 내부 지정 경로로 복사
COPY build/libs/busan-0.0.1-SNAPSHOT.jar busan-0.0.1-SNAPSHOT.jar

# JAR 실행 명령어 지정
ENTRYPOINT ["java", "-jar", "busan-0.0.1-SNAPSHOT.jar"]
