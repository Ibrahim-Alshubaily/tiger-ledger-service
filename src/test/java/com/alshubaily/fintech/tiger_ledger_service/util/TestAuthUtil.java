package com.alshubaily.fintech.tiger_ledger_service.util;

import com.alshubaily.fintech.tiger_ledger_service.model.Auth.request.LoginRequest;
import com.alshubaily.fintech.tiger_ledger_service.model.Auth.request.SignupRequest;
import com.alshubaily.fintech.tiger_ledger_service.model.Auth.response.LoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.http.HttpStatus;


import static com.alshubaily.fintech.tiger_ledger_service.e2e_test.BaseE2ETest.SERVER_ENDPOINT;
import static org.assertj.core.api.Assertions.assertThat;

public class TestAuthUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();
    public static int signup(String randomString, CloseableHttpClient client) throws Exception {
        String payload = objectMapper.writeValueAsString(
                new SignupRequest(randomString, randomString, randomString));

        HttpPost request = new HttpPost(SERVER_ENDPOINT + "/auth/signup");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(payload));
        HttpRequestUtil.RequestResult response = HttpRequestUtil.execute(client, request);
        return response.status();
    }

    public static HttpRequestUtil.RequestResult login(LoginRequest loginRequest, CloseableHttpClient client) throws Exception {
        String payload = objectMapper.writeValueAsString(loginRequest);

        HttpPost request = new HttpPost(SERVER_ENDPOINT + "/auth/login");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(payload));

        return HttpRequestUtil.execute(client, request);
    }

    public static String GetAuthToken(String userIdentifier, CloseableHttpClient client) throws Exception {
        HttpRequestUtil.RequestResult resp = login(
                new LoginRequest(userIdentifier, userIdentifier, userIdentifier),
                client);
        return objectMapper.readValue(resp.body(), LoginResponse.class).token();
    }

    public static String SignUpAndGetToken(String userIdentifier, CloseableHttpClient client) throws Exception {
        int status = signup(userIdentifier, client);
        assertThat(status).isEqualTo(HttpStatus.OK.value());
        return GetAuthToken(userIdentifier, client);
    }
}
