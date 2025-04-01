package com.alshubaily.fintech.tiger_ledger_service.e2e_test;

import com.alshubaily.fintech.tiger_ledger_service.util.HttpRequestUtil;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static com.alshubaily.fintech.tiger_ledger_service.util.TestAccountUtil.CreateAccount;
import static com.alshubaily.fintech.tiger_ledger_service.util.TestAuthUtil.GetAuthToken;
import static com.alshubaily.fintech.tiger_ledger_service.util.TestAuthUtil.signup;
import static org.assertj.core.api.Assertions.assertThat;

public class AuthorizationTests extends BaseE2ETest {

    @Test
    void accountOwnerIsAuthorized() throws Exception {
        String userIdentifier = UUID.randomUUID().toString();
        String accountId = String.valueOf(CreateAccount(userIdentifier, httpClient));

        String token = GetAuthToken(userIdentifier, httpClient);

        // Get Account details.
        HttpGet request = new HttpGet(SERVER_ENDPOINT + "/accounts/" + accountId);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Bearer " + token);
        HttpRequestUtil.RequestResult response = HttpRequestUtil.execute(httpClient, request);
        assertThat(response.status()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void NonAccountOwnerIsNotAuthorized() throws Exception {

        String userOneIdentifier = UUID.randomUUID().toString();
        String accountId = String.valueOf(CreateAccount(userOneIdentifier, httpClient));

        String userTwoIdentifier = UUID.randomUUID().toString();
        signup(userTwoIdentifier, httpClient);

        String userTwoToken = GetAuthToken(userTwoIdentifier, httpClient);

        // Get Account details.
        HttpGet request = new HttpGet(SERVER_ENDPOINT + "/accounts/" + accountId);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Bearer " + userTwoToken);
        HttpRequestUtil.RequestResult response = HttpRequestUtil.execute(httpClient, request);
        assertThat(response.status()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
