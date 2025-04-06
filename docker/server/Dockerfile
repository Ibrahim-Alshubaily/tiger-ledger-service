FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./

RUN ./gradlew dependencies --no-daemon

RUN mkdir -p src/main/resources && \
  keytool -genkeypair \
    -alias tiger-ledger \
    -keyalg RSA \
    -keysize 2048 \
    -storetype PKCS12 \
    -keystore src/main/resources/keystore.p12 \
    -validity 3650 \
    -storepass changeit \
    -keypass changeit \
    -dname "CN=localhost, OU=Dev, O=TigerLedger, L=Riyadh, ST=Riyadh, C=SA"

COPY . .

RUN ./gradlew clean build -x test --no-daemon


FROM eclipse-temurin:17-jre AS runner

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8443

ENTRYPOINT ["java", "-XX:+UseG1GC", "-jar", "app.jar"]
