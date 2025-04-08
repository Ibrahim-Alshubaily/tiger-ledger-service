FROM gradle:8.2.1-jdk17 AS builder

WORKDIR /app
COPY . .
RUN ./gradlew clean sparkJar --no-daemon

FROM spark:3.5.0-java17

COPY --from=builder /app/build/libs/spark-streaming-job-0.0.1-SNAPSHOT.jar /opt/spark/app.jar

ENV SPARK_HOME=/opt/spark

ENTRYPOINT ["/opt/spark/bin/spark-submit", \
  "--master", "local[*]", \
  "--conf", "spark.driver.extraJavaOptions=-Divy.cache.dir=/opt/spark/ivy -Divy.home=/opt/spark/ivy", \
  "--conf", "spark.executor.extraJavaOptions=-Divy.cache.dir=/opt/spark/ivy -Divy.home=/opt/spark/ivy", \
  "--packages", "org.apache.spark:spark-sql-kafka-0-10_2.12:3.3.0,org.apache.kafka:kafka-clients:3.3.0", \
  "/opt/spark/app.jar"]
