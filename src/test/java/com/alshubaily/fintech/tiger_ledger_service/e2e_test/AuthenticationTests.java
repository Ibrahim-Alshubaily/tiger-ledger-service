package com.alshubaily.fintech.tiger_ledger_service.e2e_test;

import static com.alshubaily.fintech.tiger_ledger_service.util.TestAuthUtil.GetAuthToken;
import static com.alshubaily.fintech.tiger_ledger_service.util.TestAuthUtil.login;
import static com.alshubaily.fintech.tiger_ledger_service.util.TestAuthUtil.signup;
import static org.assertj.core.api.Assertions.assertThat;

import com.alshubaily.fintech.tiger_ledger_service.model.Auth.request.LoginRequest;
import com.alshubaily.fintech.tiger_ledger_service.util.*;
import java.util.UUID;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class AuthenticationTests extends BaseE2ETest {

    @Test
    void testAuthentication() throws Exception {

        // Signup
        String userIdentifier = UUID.randomUUID().toString();
        int status = signup(userIdentifier, httpClient);
        assertThat(status).isEqualTo(HttpStatus.OK.value());

        // Login
        String token = GetAuthToken(userIdentifier, httpClient);
        assertThat(token).isNotNull();

        // Access an endpoint that requires an access token.
        HttpGet request = new HttpGet(SERVER_ENDPOINT + "/accounts");
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Bearer " + token);
        HttpRequestUtil.RequestResult response = HttpRequestUtil.execute(httpClient, request);
        assertThat(response.status()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void testInvalidLoginUsername() throws Exception {
        String userIdentifier = UUID.randomUUID().toString();
        LoginRequest randomUser = new LoginRequest(userIdentifier, userIdentifier, userIdentifier);
        assertThat(login(randomUser, httpClient).status())
                .isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void testInvalidLoginPassword() throws Exception {
        String userIdentifier = UUID.randomUUID().toString();
        signup(userIdentifier, httpClient);

        LoginRequest testUser = new LoginRequest(userIdentifier, "WRONG_PASSWORD", userIdentifier);
        assertThat(login(testUser, httpClient).status()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void testInvalidToken() throws Exception {
        HttpGet request = new HttpGet(SERVER_ENDPOINT + "/accounts");
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Bearer " + UUID.randomUUID());
        HttpRequestUtil.RequestResult response = HttpRequestUtil.execute(httpClient, request);
        assertThat(response.status()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
