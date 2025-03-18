package com.alshubaily.fintech.tiger_ledger_service.load_test;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@State(Scope.Thread)
public class TransferLoadTest {
    private static final String BASE_URL = "http://localhost:8080/api/accounts";
    private static final HttpClient client = HttpClient.newHttpClient();

    @Setup(Level.Trial)
    public void setup() throws Exception {
        makeRequest("?accountId=1");
    }

    @Benchmark
    public void testDeposit(RequestSuccessCounter counters) throws Exception {
        counters.totalRequestsCount++;
        boolean success = makeRequest("/deposit?accountId=1&amount=1.00");
        if (!success) {
            counters.failedRequestsCount++;
        }
    }

    private boolean makeRequest(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200 && response.body().contains("true");
    }

    @AuxCounters(AuxCounters.Type.EVENTS)
    @State(Scope.Thread)
    public static class RequestSuccessCounter {
        public int failedRequestsCount = 0;
        public int totalRequestsCount = 0;

    }
}
