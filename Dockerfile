# 빌드 시점에 사용할 환경 변수 ARG 정의
ARG JWT_SECRET_KEY=${JWT_SECRET_KEY}

# 빌드 스테이지
FROM gradle:7.2-jdk17 AS build
WORKDIR /app

#  gradlew 스크립트와 gradle 폴더 복사
COPY gradlew .
COPY gradle gradle

# 소스 코드와 Gradle 설정 파일을 이미지로 복사
COPY src ./src
COPY build.gradle .
COPY settings.gradle .

# 런타임에 사용할 환경 변수 ENV 설정
ENV JWT_SECRET_KEY=$JWT_SECRET_KEY

# 애플리케이션 빌드
RUN chmod +x ./gradlew
RUN ./gradlew clean build --no-daemon

# 런타임 스테이지
FROM openjdk:17

# 컨테이너 내에서 애플리케이션 파일을 저장할 경로 설정
WORKDIR /app

# 호스트 시스템의 빌드 결과물인 JAR 파일을 컨테이너의 작업 디렉토리로 복사합니다.
COPY --from=build /app/build/libs/Groomy-IDE-0.0.1-SNAPSHOT.jar app.jar

# 컨테이너가 시작될 때 실행될 명령어를 정의합니다.
# JAR 파일을 실행합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]

# 애플리케이션이 사용할 포트를 지정합니다. 예를 들어, 8080 포트를 사용한다고 가정합니다.
EXPOSE 8080
