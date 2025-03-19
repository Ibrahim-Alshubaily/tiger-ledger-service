package com.alshubaily.fintech.tiger_ledger_service.load_test;

import org.openjdk.jmh.annotations.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Fork(3)
@State(Scope.Thread)
@BenchmarkMode(Mode.All)
public class TransferLoadTest {

    private static final String BASE_URL = "http://localhost:8080/api/accounts";
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(1))
            .version(HttpClient.Version.HTTP_2)
            .build();

    @Setup(Level.Trial)
    public void setup() {
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

    private boolean makeRequest(String endpoint) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .timeout(Duration.ofMillis(1000))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 && response.body().contains("true");
        } catch (Exception e) {
            System.err.println("Request failed: " + e.getMessage());
            return false;
        }
    }

    @AuxCounters(AuxCounters.Type.EVENTS)
    @State(Scope.Thread)
    public static class RequestSuccessCounter {
        public int failedRequestsCount = 0;
        public int totalRequestsCount = 0;
    }
}
