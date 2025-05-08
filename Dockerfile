
FROM gradle:8.5-jdk17 AS builder
WORKDIR /home/app

COPY build.gradle settings.gradle ./
COPY gradle ./gradle

RUN gradle dependencies --no-daemon || return 0

COPY . .
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /home/app/build/libs/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]