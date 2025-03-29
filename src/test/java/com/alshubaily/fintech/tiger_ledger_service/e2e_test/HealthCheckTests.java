package com.alshubaily.fintech.tiger_ledger_service.e2e_test;

import com.alshubaily.fintech.tiger_ledger_service.util.HttpRequestUtil;
import com.alshubaily.fintech.tiger_ledger_service.util.HttpRequestUtil.*;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class HealthCheckTests extends BaseE2ETest {

    @Test
    void testHealthCheck() throws Exception {
        try {
            HttpGet request = new HttpGet(SERVER_ENDPOINT + "/health");
            RequestResult result = HttpRequestUtil.execute(httpClient, request);
            assertThat(result.status()).isEqualTo(HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
