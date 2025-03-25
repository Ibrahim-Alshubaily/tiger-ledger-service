package com.alshubaily.fintech.tiger_ledger_service.service;

import com.alshubaily.fintech.tiger_ledger_service.db.account.Account;
import com.alshubaily.fintech.tiger_ledger_service.db.account.AccountRepository;
import com.alshubaily.fintech.tiger_ledger_service.db.user.User;
import com.alshubaily.fintech.tiger_ledger_service.db.user.UserRepository;
import com.alshubaily.fintech.tiger_ledger_service.model.Transaction;
import com.alshubaily.fintech.tiger_ledger_service.model.account.request.DepositRequest;
import com.alshubaily.fintech.tiger_ledger_service.model.account.request.TransferRequest;
import com.alshubaily.fintech.tiger_ledger_service.model.account.request.WithdrawRequest;
import com.alshubaily.fintech.tiger_ledger_service.model.account.response.CreateAccountResponse;
import com.alshubaily.fintech.tiger_ledger_service.model.account.response.GetAccountResponse;
import com.alshubaily.fintech.tiger_ledger_service.model.account.response.TransferResponse;
import com.alshubaily.fintech.tiger_ledger_service.util.AccountUtil;
import com.alshubaily.fintech.tiger_ledger_service.util.CurrencyUtil;
import com.alshubaily.fintech.tiger_ledger_service.util.SecurityUtil;
import com.tigerbeetle.*;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@Service
public class AccountService {

    private final Client client;
    private static final BigInteger CASH_ACCOUNT_ID = BigInteger.valueOf(Long.MAX_VALUE);
    private static final int LEDGER = 1;
    private static final int CODE = 1;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;


    public AccountService(Client client, UserRepository userRepository, AccountRepository accountRepository) throws Exception {
        this.client = client;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;

        createAccount(UInt128.asBytes(CASH_ACCOUNT_ID), 0L);
    }

    public CreateAccountResponse createAccount() throws Exception {
        byte[] accountId = UInt128.id();
        long userId = SecurityUtil.getAuthenticatedUserId();

        CreateAccountResultBatch errors = createAccount(accountId, userId);
        if (errors.next()) {
            throw new Exception("Failed to create account: " + errors.getResult());
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found: " + userId));

        Account account = new Account();
        account.setAccountId(UInt128.asBigInteger(accountId));
        account.setOwner(user);
        accountRepository.save(account);
        return new CreateAccountResponse(account.getAccountId());
    }

    private CreateAccountResultBatch createAccount(byte[] accountId, long userId) {
        AccountBatch accounts = new AccountBatch(1);
        accounts.add();
        accounts.setId(accountId);
        accounts.setLedger(LEDGER);
        accounts.setCode(CODE);
        accounts.setFlags(AccountFlags.NONE);
        accounts.setUserData64(userId);
        return client.createAccounts(accounts);
    }

    public List<GetAccountResponse> getAccounts() {
        long userId = SecurityUtil.getAuthenticatedUserId();
        return accountRepository.findAllByOwnerId(userId).stream()
                .map(acc -> new GetAccountResponse(acc.getAccountId()))
                .toList();
    }

    public GetAccountResponse getAccountDetails(BigInteger accountId) {
        try {
            Account account = accountRepository.findByAccountId(accountId)
                    .orElseThrow(() -> new IllegalArgumentException("Account not found in DB: " + accountId));

            User user = account.getOwner();
            Instant createdAt = account.getCreatedAt();

            IdBatch idBatch = new IdBatch(1);
            idBatch.add(UInt128.asBytes(accountId));
            AccountBatch batch = client.lookupAccounts(idBatch);

            if (batch.getLength() == 0) {
                throw new IllegalArgumentException("Account not found in TigerBeetle: " + accountId);
            }

            batch.next();
            long balanceHalala = batch.getCreditsPosted().longValue() - batch.getDebitsPosted().longValue();
            double balanceSar = CurrencyUtil.halalaToSar(balanceHalala);
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));
            String balanceFormatted =  currencyFormat.format(balanceSar);
            return new GetAccountResponse(
                    accountId,
                    balanceFormatted,
                    balanceSar,
                    createdAt,
                    user.getId(),
                    user.getUsername()
            );

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get account: " + e.getMessage(), e);
        }
    }

    private TransferResponse transfer(BigInteger debitAccountId, BigInteger creditAccountId, double amountSar) {
        long amountHalala = CurrencyUtil.sarToHalala(amountSar);
        byte[] transactionIdBytes = UInt128.id();

        TransferBatch transfers = new TransferBatch(1);
        transfers.add();
        transfers.setId(transactionIdBytes);
        transfers.setDebitAccountId(UInt128.asBytes(debitAccountId));
        transfers.setCreditAccountId(UInt128.asBytes(creditAccountId));
        transfers.setAmount(amountHalala);
        transfers.setLedger(LEDGER);
        transfers.setCode(CODE);
        transfers.setFlags(TransferFlags.NONE);

        CreateTransferResultBatch result = client.createTransfers(transfers);

        if (result.getLength() > 0) {
            result.next();
            throw new IllegalStateException("Transfer rejected by TigerBeetle: " + result.getResult());
        }

        return new TransferResponse(
                UInt128.asBigInteger(transactionIdBytes).toString(),
                Instant.now()
        );
    }

    public TransferResponse transfer(TransferRequest request) {
        return transfer(request.debitAccountId(), request.creditAccountId(), request.amount());
    }

    public TransferResponse deposit(DepositRequest request) {
        return transfer(CASH_ACCOUNT_ID, request.accountId(), request.amount());
    }

    public TransferResponse withdraw(WithdrawRequest request) {
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


