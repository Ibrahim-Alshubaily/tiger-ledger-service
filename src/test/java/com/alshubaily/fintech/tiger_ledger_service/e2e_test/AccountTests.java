package com.alshubaily.fintech.tiger_ledger_service.e2e_test;

import com.alshubaily.fintech.tiger_ledger_service.model.account.response.GetAccountResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigInteger;
import java.util.UUID;

import static com.alshubaily.fintech.tiger_ledger_service.util.TestAccountUtil.*;
import static com.alshubaily.fintech.tiger_ledger_service.util.TestAuthUtil.SignUpAndGetToken;
import static org.assertj.core.api.Assertions.assertThat;

public class AccountTests extends BaseE2ETest {

    @Test
    void createAccount_shouldSucceed() throws Exception {
        String token = SignUpAndGetToken(UUID.randomUUID().toString(), httpClient);
        BigInteger accountId = CreateAccount(token, httpClient);
        assertThat(accountId).isNotNull();
    }

    @Test
    void getAccount_shouldReturnAccountDetails() throws Exception {
        String ownerName = UUID.randomUUID().toString();
        String token = SignUpAndGetToken(ownerName, httpClient);
        BigInteger accountId = CreateAccount(token, httpClient);

        GetAccountResponse response = GetAccount(accountId.toString(), token, httpClient);
        assertThat(response.ownerName()).isEqualTo(ownerName);
    }

    @Test
    void getAccounts_shouldReturnUserAccounts() throws Exception {
        String ownerName = UUID.randomUUID().toString();
        String token = SignUpAndGetToken(ownerName, httpClient);

        CreateAccount(token, httpClient);
        CreateAccount(token, httpClient);

        GetAccountResponse[] accounts = GetAccounts(token, httpClient);
        assertThat(accounts).hasSize(2);

        for (GetAccountResponse account : accounts) {
            assertThat(account.ownerName()).isEqualTo(ownerName);
        }
    }

    @Test
    void getAccount_shouldFailIfUserDoesNotOwnAccount() throws Exception {
        String ownerId = UUID.randomUUID().toString();
        String intruderId = UUID.randomUUID().toString();

        String ownerToken = SignUpAndGetToken(ownerId, httpClient);
        BigInteger accountId = CreateAccount(ownerToken, httpClient);

        String intruderToken = SignUpAndGetToken(intruderId, httpClient);


        int status = GetAccountResponse(accountId.toString(), intruderToken, httpClient).status();
        assertThat(status).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void getAccount_shouldFailIfAccountDoesNotExist() throws Exception {
        String token = SignUpAndGetToken(UUID.randomUUID().toString(), httpClient);
        String nonexistentId = "1234";

        int status = GetAccountResponse(nonexistentId, token, httpClient).status();
        assertThat(status).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
