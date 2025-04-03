package com.alshubaily.fintech.tiger_ledger_service.util;

import com.alshubaily.fintech.tiger_ledger_service.model.transaction.request.DepositRequest;
import com.alshubaily.fintech.tiger_ledger_service.model.transaction.request.TransferRequest;
import com.alshubaily.fintech.tiger_ledger_service.model.transaction.request.WithdrawRequest;
import com.alshubaily.fintech.tiger_ledger_service.model.transaction.response.GetTransactionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.math.BigInteger;

import static com.alshubaily.fintech.tiger_ledger_service.e2e_test.BaseE2ETest.SERVER_ENDPOINT;

public class TransactionTestUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    public static HttpRequestUtil.RequestResult deposit(double amount, String accountId, String token, CloseableHttpClient client) throws Exception {
        HttpPost request = new HttpPost(SERVER_ENDPOINT + "/accounts/" + accountId + "/transactions/deposit");
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Bearer " + token);

        String payload = objectMapper.writeValueAsString(
                new DepositRequest(amount));
        request.setEntity(new StringEntity(payload));
        return HttpRequestUtil.execute(client, request);
    }

    public static HttpRequestUtil.RequestResult withdraw(double amount, String accountId, String token, CloseableHttpClient client) throws Exception {
        HttpPost request = new HttpPost(SERVER_ENDPOINT + "/accounts/" + accountId + "/transactions/withdraw");
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Bearer " + token);

        String payload = objectMapper.writeValueAsString(
                new WithdrawRequest(amount));
        request.setEntity(new StringEntity(payload));
        return HttpRequestUtil.execute(client, request);
    }

    public static HttpRequestUtil.RequestResult transfer(double amount, String from, String to,  String token, CloseableHttpClient client) throws Exception {
        HttpPost request = new HttpPost(SERVER_ENDPOINT + "/accounts/" + from + "/transactions/transfer");
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Bearer " + token);

        String payload = objectMapper.writeValueAsString(
                new TransferRequest(new BigInteger(to), amount));
        request.setEntity(new StringEntity(payload));
        return HttpRequestUtil.execute(client, request);
    }


    public static GetTransactionResponse[] listTransactions(String accountId, String token, CloseableHttpClient client) throws Exception {
        HttpGet request = new HttpGet(SERVER_ENDPOINT + "/accounts/" + accountId + "/transactions");
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Bearer " + token);
        HttpRequestUtil.RequestResult resp  = HttpRequestUtil.execute(client, request);
        return objectMapper.readValue(resp.body(), GetTransactionResponse[].class);
    }



}
