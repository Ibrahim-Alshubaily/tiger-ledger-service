package com.alshubaily.fintech.tiger_ledger_service.spark;

import com.alshubaily.fintech.tiger_ledger_service.eventbus.TransactionEvent;
import com.alshubaily.fintech.tiger_ledger_service.spark.util.SchemaUtil;
import com.alshubaily.fintech.tiger_ledger_service.spark.metrics.PrometheusMetricsExporter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.Trigger;
import org.apache.spark.sql.types.StructType;

import java.util.concurrent.TimeUnit;

import static org.apache.spark.sql.functions.*;

public class Main {
    public static void main(String[] args) throws Exception {
        PrometheusMetricsExporter.start(9095);

        SparkSession spark = SparkSession.builder()
                .appName("TigerLedgerMetricsStream")
                .master("local[*]")
                .getOrCreate();

        Dataset<Row> stream = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "kafka:9092")
                .option("subscribe", "transactions")
                .option("startingOffsets", "earliest")
                .load();

        StructType schema = SchemaUtil.schemaFromRecord(TransactionEvent.class);

        Dataset<Row> transactions = stream.select(
                from_json(col("value").cast("string"), schema).as("data")
        ).select(
                col("data.amount").alias("amount"),
                col("data.transactionType").alias("transactionType"),
                col("data.timestamp").alias("timestamp").cast("timestamp")
        ).withWatermark("timestamp", "1 minute");

        Dataset<Row> metrics = transactions
                .groupBy(window(col("timestamp"), "1 minute"), col("transactionType"))
                .agg(
                        count("*").alias("transaction_count"),
                        sum(col("amount").cast("double").divide(100.0)).alias("total_amount")
                )
                .select(
                        col("transactionType").alias("transaction_type"),
                        col("transaction_count"),
                        col("total_amount")
                );

        metrics.writeStream()
                .outputMode("append")
                .trigger(Trigger.ProcessingTime(1, TimeUnit.MINUTES))
                .option("checkpointLocation", "file:/checkpoints/prometheus-metrics")
                .foreachBatch((batchDF, batchId) -> {
                    batchDF.collectAsList().forEach(PrometheusMetricsExporter::export);
                })
                .start()
                .awaitTermination();
    }
}
