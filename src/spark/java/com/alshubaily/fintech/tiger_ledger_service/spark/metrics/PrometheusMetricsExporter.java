package com.alshubaily.fintech.tiger_ledger_service.spark.metrics;

import io.prometheus.client.Counter;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import org.apache.spark.sql.Row;

import java.io.IOException;

public class PrometheusMetricsExporter {

    private static final Counter transactionCounter = Counter.build()
            .name("transaction_count_total")
            .help("Total number of transactions per type")
            .labelNames("transaction_type")
            .register();

    private static final Counter transactionAmount = Counter.build()
            .name("transaction_amount_total")
            .help("Total transaction amount per type")
            .labelNames("transaction_type")
            .register();

    private static HTTPServer server;

    public static void start(int port) throws IOException {
        DefaultExports.initialize(); // JVM-level metrics
        server = new HTTPServer(port);
    }

    public static void export(Row row) {
        String type = row.getAs("transaction_type");
        long count = row.getAs("transaction_count");
        double amount = row.getAs("total_amount");

        transactionCounter.labels(type).inc(count);
        transactionAmount.labels(type).inc(amount);
    }
}
