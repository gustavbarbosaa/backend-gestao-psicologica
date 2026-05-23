FROM gradle:jdk25-alpine AS builder

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY src src

RUN gradle clean bootJar --no-daemon --stacktrace

FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
