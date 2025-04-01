package com.alshubaily.fintech.tiger_ledger_service.util;

import com.alshubaily.fintech.tiger_ledger_service.model.account.response.CreateAccountResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.http.HttpStatus;

import java.math.BigInteger;

import static com.alshubaily.fintech.tiger_ledger_service.e2e_test.BaseE2ETest.SERVER_ENDPOINT;
import static com.alshubaily.fintech.tiger_ledger_service.util.TestAuthUtil.GetAuthToken;
import static com.alshubaily.fintech.tiger_ledger_service.util.TestAuthUtil.signup;
import static org.assertj.core.api.Assertions.assertThat;

public class TestAccountUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static BigInteger CreateAccount(String userIdentifier, CloseableHttpClient httpClient) throws Exception {

        int status = signup(userIdentifier, httpClient);
        assertThat(status).isEqualTo(HttpStatus.OK.value());

        // Login
        String token = GetAuthToken(userIdentifier, httpClient);
        assertThat(token).isNotNull();

        // Create Account
        HttpPost request = new HttpPost(SERVER_ENDPOINT + "/accounts");
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Bearer " + token);
        HttpRequestUtil.RequestResult response = HttpRequestUtil.execute(httpClient, request);
        assertThat(response.status()).isEqualTo(HttpStatus.OK.value());

        return objectMapper.readValue(response.body(), CreateAccountResponse.class).accountId();
    }


}
