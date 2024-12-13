FROM gradle:jdk17-corretto-al2023 AS builder

# Gradle 캐시 디렉토리 설정
ENV GRADLE_USER_HOME=/home/gradle/.gradle


WORKDIR /usr/app/

COPY . .

RUN chmod +x gradlew

# RUN ./gradlew build
# Gradle 빌드 실행 (빌드 결과를 캐시 가능하게 유지)
RUN ./gradlew build --no-daemon --stacktrace

FROM openjdk:17

ENV JAR_FILE=*.jar
ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME
COPY --from=builder $APP_HOME/build/libs/$JAR_FILE ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]