FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

COPY ../../gradle gradle
COPY ../../build.gradle settings.gradle gradlew ./
RUN ./gradlew dependencies --no-daemon

COPY ../.. .

RUN ./gradlew clean build -x test --no-daemon


FROM eclipse-temurin:17-jre AS runner

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
