package com.alshubaily.fintech.tiger_ledger_service.service;

import com.alshubaily.fintech.tiger_ledger_service.model.Transaction;
import com.alshubaily.fintech.tiger_ledger_service.model.account.request.DepositRequest;
import com.alshubaily.fintech.tiger_ledger_service.model.account.request.TransferRequest;
import com.alshubaily.fintech.tiger_ledger_service.model.account.request.WithdrawRequest;
import com.alshubaily.fintech.tiger_ledger_service.model.account.response.CreateAccountResponse;
import com.alshubaily.fintech.tiger_ledger_service.util.AccountUtil;
import com.alshubaily.fintech.tiger_ledger_service.util.CurrencyUtil;
import com.tigerbeetle.*;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@Service
public class AccountService {

    private final Client client;
    private static final long CASH_ACCOUNT_ID = Long.MAX_VALUE;
    private static final int LEDGER = 1;
    private static final int CODE = 1;

    public AccountService(Client client) throws RequestException {
        this.client = client;
        createAccount(UInt128.asBytes(CASH_ACCOUNT_ID));
    }

    public CreateAccountResponse createAccount() {
        byte[] id = UInt128.id();
        createAccount(id);
        return new CreateAccountResponse(ByteBuffer.wrap(id).getLong());
    }
    private void createAccount(byte[] accountId) throws RequestException {
        AccountBatch accounts = new AccountBatch(1);
        accounts.add();
        accounts.setId(accountId);
        accounts.setLedger(LEDGER);
        accounts.setCode(CODE);
        accounts.setFlags(AccountFlags.NONE);
        CreateAccountResultBatch errors = client.createAccounts(accounts);
        if (errors.next()) {
            throw new RuntimeException("Failed to create account: "+ errors.getResult());
        }
    }

    public AccountBatch getAccount(long accountId) throws RequestException {
        IdBatch idBatch = new IdBatch(1);
        idBatch.add(UInt128.asBytes(accountId));

        AccountBatch accounts = client.lookupAccounts(idBatch);
        if (accounts.getLength() == 0) {
            return null;
        }
        accounts.next();
        return accounts;
    }

    public String getAccountBalance(long accountId) throws RequestException {
        AccountBatch account = getAccount(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }

        long balanceHalala = account.getCreditsPosted().longValue() - account.getDebitsPosted().longValue();
        double balanceSar = CurrencyUtil.halalaToSar(balanceHalala);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));
        return currencyFormat.format(balanceSar);
    }


    public CompletableFuture<Boolean> transfer(long debitAccountId, long creditAccountId, double amountSar) {

        long amountHalala = CurrencyUtil.sarToHalala(amountSar);
        TransferBatch transfers = new TransferBatch(1);
        transfers.add();
        transfers.setId(UInt128.id());
        transfers.setDebitAccountId(UInt128.asBytes(debitAccountId));
        transfers.setCreditAccountId(UInt128.asBytes(creditAccountId));
        transfers.setAmount(amountHalala);
        transfers.setLedger(LEDGER);
        transfers.setCode(CODE);
        transfers.setFlags(TransferFlags.NONE);

        return client.createTransfersAsync(transfers)
                .thenApply(response -> response.getLength() == 0)
                .exceptionally(e -> {
                    System.err.println("Transfer failed: " + e.getMessage());
                    return false;
                });
    }

    public CompletableFuture<Boolean> transfer(TransferRequest request) {
        return transfer(request.debitAccountId(), request.creditAccountId(), request.amount());
    }

    public CompletableFuture<Boolean> deposit(DepositRequest request) {
        return transfer(CASH_ACCOUNT_ID, request.accountId(), request.amount());
    }

    public CompletableFuture<Boolean> withdraw(WithdrawRequest request) {
        return transfer(request.accountId(), CASH_ACCOUNT_ID, request.amount());
    }

    public List<Transaction> getTransactionHistory(long accountId) {
        AccountFilter filter = new AccountFilter();
        filter.setAccountId(accountId);
        filter.setLimit(10); // TODO: Pagination
        filter.setDebits(true);
        filter.setCredits(true);

        TransferBatch tb = client.getAccountTransfers(filter);
        List<Transaction> resp = new ArrayList<>();
        while (tb.next()) {
            resp.add(new Transaction(
                    AccountUtil.toLong(tb.getDebitAccountId()),
                    AccountUtil.toLong(tb.getCreditAccountId()),
                    CurrencyUtil.halalaToSar(tb.getAmount()),
                    Instant.ofEpochSecond(0, tb.getTimestamp())
                    ));
        }
        return resp;
    }
}


