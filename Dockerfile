FROM gradle:jdk17-corretto-al2023 AS builder

WORKDIR /usr/app/

COPY . .

RUN chmod +x gradlew

RUN ./gradlew build

FROM openjdk:17
ENV JAR_FILE=*.jar
ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME
COPY --from=builder $APP_HOME/build/libs/$JAR_FILE .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "${JAR_FILE}"]