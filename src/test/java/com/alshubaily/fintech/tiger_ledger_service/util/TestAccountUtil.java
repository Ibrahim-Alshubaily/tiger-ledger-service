package com.alshubaily.fintech.tiger_ledger_service.util;

import com.alshubaily.fintech.tiger_ledger_service.model.account.response.CreateAccountResponse;
import com.alshubaily.fintech.tiger_ledger_service.model.account.response.GetAccountResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.http.HttpStatus;

import java.math.BigInteger;

import static com.alshubaily.fintech.tiger_ledger_service.e2e_test.BaseE2ETest.SERVER_ENDPOINT;


import static org.assertj.core.api.Assertions.assertThat;

public class TestAccountUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static BigInteger CreateAccount(String token, CloseableHttpClient httpClient) throws Exception {
        // Create Account
        HttpPost request = new HttpPost(SERVER_ENDPOINT + "/accounts");
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Bearer " + token);
        HttpRequestUtil.RequestResult response = HttpRequestUtil.execute(httpClient, request);
        assertThat(response.status()).isEqualTo(HttpStatus.OK.value());

        return objectMapper.readValue(response.body(), CreateAccountResponse.class).accountId();
    }

    public static HttpRequestUtil.RequestResult GetAccountResponse(String accountId, String token, CloseableHttpClient httpClient) throws Exception {
        HttpGet request = new HttpGet(SERVER_ENDPOINT + "/accounts/" + accountId);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Bearer " + token);
        return HttpRequestUtil.execute(httpClient, request);
    }

    public static GetAccountResponse GetAccount(String accountId, String token, CloseableHttpClient httpClient) throws Exception {
        HttpRequestUtil.RequestResult response = GetAccountResponse(accountId, token, httpClient);
        assertThat(response.status()).isEqualTo(HttpStatus.OK.value());

        return objectMapper.readValue(response.body(), GetAccountResponse.class);
    }

    public static GetAccountResponse[] GetAccounts(String token, CloseableHttpClient httpClient) throws Exception {
        HttpGet request = new HttpGet(SERVER_ENDPOINT + "/accounts");
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Bearer " + token);
        HttpRequestUtil.RequestResult response = HttpRequestUtil.execute(httpClient, request);
        assertThat(response.status()).isEqualTo(HttpStatus.OK.value());

        return objectMapper.readValue(response.body(), GetAccountResponse[].class);
    }

    public static double GetAccountBalance(String accountId, String token, CloseableHttpClient httpClient) throws Exception {
        return GetAccount(accountId, token, httpClient).balanceSar();
    }
}
