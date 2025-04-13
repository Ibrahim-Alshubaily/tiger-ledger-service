FROM gradle:8.2.1-jdk17 AS builder

WORKDIR /app
COPY . .
RUN ./gradlew clean sparkJar --no-daemon

FROM spark:3.5.0-java17

COPY --from=builder /app/build/libs/spark-metrics-stream-0.0.1-SNAPSHOT.jar /opt/spark/app.jar
COPY --from=builder /app/build/libs/spark-packages.txt /opt/spark/spark-packages.txt

ENV SPARK_HOME=/opt/spark

USER root

COPY /docker/spark/entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
