package com.alshubaily.fintech.tiger_ledger_service.e2e_tests;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import javax.net.ssl.SSLContext;

public class TigerLedgerE2ETests {

    private static CloseableHttpClient httpClient;
    private static final String HEALTH_ENDPOINT = "https://localhost:8443/api/v1/health";

    @BeforeAll
    static void setup() throws Exception {
        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial((chain, authType) -> true)
                .build();

        httpClient = HttpClients.custom()
                .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                        .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                                .setSslContext(sslContext)
                                .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                                .build())
                        .build())
                .build();
    }

    @Test
    void testHealthCheck() throws Exception {
        HttpGet request = new HttpGet(HEALTH_ENDPOINT);

        try (ClassicHttpResponse response = httpClient.execute(request)) {
            assertThat(response.getCode()).isEqualTo(200);
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            System.out.println("Request failed: " + e.getMessage());
            throw e;
        }
    }
}