# Сборка: контекст — корень репозитория, например: docker build -f app/Dockerfile .
# На Render: Root Directory пустой, путь к Dockerfile — app/Dockerfile
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY app/gradle gradle
COPY app/build.gradle.kts .
COPY app/settings.gradle.kts .
COPY app/gradle.lockfile .
COPY app/gradlew .

RUN chmod +x gradlew

RUN ./gradlew --no-daemon dependencies

COPY app/src src
COPY app/config config

RUN ./gradlew --no-daemon build

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=60.0 -XX:InitialRAMPercentage=50.0"
EXPOSE 7070

CMD ["java", "-jar", "build/libs/HexletJavalin-1.0-SNAPSHOT-all.jar"]
