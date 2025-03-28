package com.alshubaily.fintech.tiger_ledger_service.e2e_test;

import com.alshubaily.fintech.tiger_ledger_service.util.HttpRequestUtil;
import com.alshubaily.fintech.tiger_ledger_service.util.HttpRequestUtil.*;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class HealthCheckTests extends BaseE2ETest {

    @Test
    void testHealthCheck() throws Exception {
        HttpGet request = new HttpGet(SERVER_ENDPOINT + "/api/v1/health");
        RequestResult result = HttpRequestUtil.execute(httpClient, request);
        assertThat(result.status()).isEqualTo(200);
    }
}
