package com.alshubaily.fintech.tiger_ledger_service.e2e_test;

import static com.alshubaily.fintech.tiger_ledger_service.util.TestAccountUtil.*;
import static com.alshubaily.fintech.tiger_ledger_service.util.TestAuthUtil.SignUpAndGetToken;
import static com.alshubaily.fintech.tiger_ledger_service.util.TransactionTestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

public class TransactionTests extends BaseE2ETest {

    @Test
    void deposit_shouldIncreaseAccountBalanceByDepositedAmount() throws Exception {
        String ownerName = UUID.randomUUID().toString();
        String token = SignUpAndGetToken(ownerName, httpClient);

        String accountId = String.valueOf(CreateAccount(token, httpClient));
        double preDepositBalance = GetAccountBalance(accountId, token, httpClient);

        double depositAmount = 100.00;
        deposit(depositAmount, accountId, token, httpClient);

        double postDepositBalance = GetAccountBalance(accountId, token, httpClient);
        assertThat(postDepositBalance - preDepositBalance).isEqualTo(depositAmount);
    }

    @Test
    void withdraw_shouldDecreaseAccountBalanceByWithdrawnAmount() throws Exception {
        String ownerName = UUID.randomUUID().toString();
        String token = SignUpAndGetToken(ownerName, httpClient);

        String accountId = String.valueOf(CreateAccount(token, httpClient));

        double preWithdrawBalance = GetAccountBalance(accountId, token, httpClient);

        double withdrawAmount = 100.00;
        withdraw(withdrawAmount, accountId, token, httpClient);

        double postWithdrawBalance = GetAccountBalance(accountId, token, httpClient);
        assertThat(preWithdrawBalance - postWithdrawBalance).isEqualTo(withdrawAmount);
    }

    @Test
    void transfer_shouldMoveFundsFromSourceToTargetAccount() throws Exception {
        String ownerName = UUID.randomUUID().toString();
        String token = SignUpAndGetToken(ownerName, httpClient);

        String from = String.valueOf(CreateAccount(token, httpClient));
        String to = String.valueOf(CreateAccount(token, httpClient));

        double preTransferSourceBalance = GetAccountBalance(from, token, httpClient);
        double preTransferTargetBalance = GetAccountBalance(to, token, httpClient);

        double transferAmount = 100.00;
        transfer(transferAmount, from, to, token, httpClient);

        double postTransferSourceBalance = GetAccountBalance(from, token, httpClient);
        double postTransferTargetBalance = GetAccountBalance(to, token, httpClient);

        assertThat(preTransferSourceBalance - postTransferSourceBalance).isEqualTo(transferAmount);
        assertThat(postTransferTargetBalance - preTransferTargetBalance).isEqualTo(transferAmount);
    }
}
