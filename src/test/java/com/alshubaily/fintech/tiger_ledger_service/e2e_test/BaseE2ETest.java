package com.alshubaily.fintech.tiger_ledger_service.e2e_test;

import com.alshubaily.fintech.tiger_ledger_service.util.HttpClientProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseE2ETest {

    protected static CloseableHttpClient httpClient;
    protected static final String SERVER_ENDPOINT = System.getenv()
            .getOrDefault("TIGER_LEDGER_SERVER_BASE_URL", "https://localhost:8443");

    @BeforeAll
    static void setupClient() throws Exception {
        httpClient = HttpClientProvider.getHttpClient();
    }

    @AfterAll
    static void closeClient() throws Exception {
        httpClient.close();
    }
}
