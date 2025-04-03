package com.alshubaily.fintech.tiger_ledger_service.e2e_test;

import com.alshubaily.fintech.tiger_ledger_service.model.transaction.Transaction;
import com.alshubaily.fintech.tiger_ledger_service.model.transaction.response.GetTransactionResponse;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.UUID;

import static com.alshubaily.fintech.tiger_ledger_service.util.TestAccountUtil.CreateAccount;
import static com.alshubaily.fintech.tiger_ledger_service.util.TestAuthUtil.SignUpAndGetToken;
import static com.alshubaily.fintech.tiger_ledger_service.util.TransactionTestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

class ListTransactionsTest extends BaseE2ETest {

    @Test
    void listTransactions_returnsDepositTransaction() throws Exception {
        String token = SignUpAndGetToken(UUID.randomUUID().toString(), httpClient);
        BigInteger accountId = CreateAccount(token, httpClient);
        double amount = 10;

        assertThat(listTransactions(accountId.toString(), token, httpClient)).isEmpty();

        deposit(amount, accountId.toString(), token, httpClient);

        GetTransactionResponse[] transactions = listTransactions(accountId.toString(), token, httpClient);
        assertThat(transactions).hasSize(1);
        assertThat(transactions[0]).isEqualTo(new GetTransactionResponse(
                Transaction.DEPOSIT,
                null,
                accountId,
                amount,
                transactions[0].timestamp()
        ));
    }

    @Test
    void listTransactions_returnsWithdrawTransaction() throws Exception {
        String token = SignUpAndGetToken(UUID.randomUUID().toString(), httpClient);
        BigInteger accountId = CreateAccount(token, httpClient);
        double amount = 10;

        withdraw(amount, accountId.toString(), token, httpClient);

        GetTransactionResponse[] transactions = listTransactions(accountId.toString(), token, httpClient);
        assertThat(transactions).hasSize(1);
        assertThat(transactions[0]).isEqualTo(new GetTransactionResponse(
                Transaction.WITHDRAW,
                accountId,
                null,
                amount,
                transactions[0].timestamp()
        ));
    }

    @Test
    void listTransactions_returnsTransferTransaction() throws Exception {
        String token = SignUpAndGetToken(UUID.randomUUID().toString(), httpClient);
        BigInteger from = CreateAccount(token, httpClient);
        BigInteger to = CreateAccount(token, httpClient);
        double amount = 10;

        transfer(amount, from.toString(), to.toString(), token, httpClient);

        GetTransactionResponse[] transactions = listTransactions(from.toString(), token, httpClient);
        assertThat(transactions).hasSize(1);
        assertThat(transactions[0]).isEqualTo(new GetTransactionResponse(
                Transaction.TRANSFER,
                from,
                to,
                amount,
                transactions[0].timestamp()
        ));
    }
}
