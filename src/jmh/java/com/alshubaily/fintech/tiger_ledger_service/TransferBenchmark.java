package com.alshubaily.fintech.tiger_ledger_service;

import com.alshubaily.fintech.tiger_ledger_service.util.HttpClientProvider;
import com.alshubaily.fintech.tiger_ledger_service.util.TestAccountUtil;
import com.alshubaily.fintech.tiger_ledger_service.util.TransactionTestUtil;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.openjdk.jmh.annotations.*;
import org.springframework.http.HttpStatus;


import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.alshubaily.fintech.tiger_ledger_service.util.TestAuthUtil.*;

@Fork(3)
@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class TransferBenchmark {

    protected CloseableHttpClient CLIENT;

    protected String TOKEN;

    protected String ACCOUNT_ID;



    @Setup(Level.Trial)
    public void setup() throws Exception {
        CLIENT = HttpClientProvider.getHttpClient();
        TOKEN = SignUpAndGetToken(UUID.randomUUID().toString(), CLIENT);
        ACCOUNT_ID = TestAccountUtil.CreateAccount(TOKEN, CLIENT).toString();
    }

    @Benchmark
    public void benchmarkDeposit(RequestSuccessCounter counters) {
        counters.totalRequestsCount++;
        if (!makeRequest()) {
            counters.failedRequestsCount++;
        }
    }

    private boolean makeRequest() {
        try {

            double randomAmount = Math.random() * 9000 + 1000;
            var response = TransactionTestUtil.deposit(randomAmount, ACCOUNT_ID, TOKEN, CLIENT);
            if (response.status() != HttpStatus.OK.value()) {
                System.err.println("Request failed with status: " + response.status());
                return false;
            }
            return true;
        } catch (Exception e) {
            System.err.println("Request failed with exception: " + e.getMessage());
            e.printStackTrace();
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
