#!/bin/bash

PACKAGES=$(cat /opt/spark/spark-packages.txt)

exec /opt/spark/bin/spark-submit \
  --master local[*] \
  --conf "spark.driver.extraJavaOptions=-Divy.cache.dir=/opt/spark/ivy -Divy.home=/opt/spark/ivy" \
  --conf "spark.executor.extraJavaOptions=-Divy.cache.dir=/opt/spark/ivy -Divy.home=/opt/spark/ivy" \
  --packages "$PACKAGES" \
  /opt/spark/app.jar
