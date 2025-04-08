package com.alshubaily.fintech.tiger_ledger_service.spark;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQueryException;

import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) throws TimeoutException, StreamingQueryException {
        SparkSession spark = SparkSession.builder()
                .appName("TigerLedgerStream")
                .getOrCreate();

        Dataset<Row> stream = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "kafka:9092")
                .option("subscribe", "transactions")
                .load();

        stream.writeStream()
                .format("console")
                .option("truncate", "false")
                .start()
                .awaitTermination();
    }
}
