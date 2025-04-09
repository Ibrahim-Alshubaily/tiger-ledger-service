package com.alshubaily.fintech.tiger_ledger_service.spark;

import com.alshubaily.fintech.tiger_ledger_service.eventbus.TransactionEvent;
import com.alshubaily.fintech.tiger_ledger_service.spark.util.SchemaUtil;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.Trigger;

import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.*;

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

        Dataset<Row> transactions = stream.selectExpr("CAST(value AS STRING) as json")
                .select(functions.from_json(
                        col("json"),
                        SchemaUtil.schemaFromRecord(TransactionEvent.class)
                ).as("data"))
                .select("data.*");

        Dataset<Row> dashboardMetrics = transactions
                .groupBy(col("transactionType"))
                .agg(
                        count("*").alias("transaction_count"),
                        sum("amount").alias("total_amount")
                );

        dashboardMetrics.writeStream()
                .format("console")
                .outputMode("update")
                .trigger(Trigger.ProcessingTime("1 minute"))
                .option("truncate", "false")
                .start()
                .awaitTermination();
    }
}
