# 1. 베이스 이미지
FROM eclipse-temurin:17-jdk-alpine

# 2. 작업 디렉토리
WORKDIR /app

# 3. Gradle 빌드 파일 복사
COPY build/libs/*.jar app.jar

# 4. 포트 노출
EXPOSE 8080

# 5. 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]