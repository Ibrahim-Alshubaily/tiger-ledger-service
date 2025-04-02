package com.alshubaily.fintech.tiger_ledger_service.service;

import com.alshubaily.fintech.tiger_ledger_service.db.account.Account;
import com.alshubaily.fintech.tiger_ledger_service.db.account.AccountRepository;
import com.alshubaily.fintech.tiger_ledger_service.db.user.User;
import com.alshubaily.fintech.tiger_ledger_service.db.user.UserRepository;
import com.alshubaily.fintech.tiger_ledger_service.model.account.response.CreateAccountResponse;
import com.alshubaily.fintech.tiger_ledger_service.model.account.response.GetAccountResponse;
import com.alshubaily.fintech.tiger_ledger_service.util.CurrencyUtil;
import com.alshubaily.fintech.tiger_ledger_service.util.SecurityUtil;
import com.tigerbeetle.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
public class AccountService {

    private final Client client;
    static final BigInteger CASH_ACCOUNT_ID = BigInteger.valueOf(Long.MAX_VALUE);
    static final int LEDGER = 1;
    static final int CODE = 1;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;


    public AccountService(Client client, UserRepository userRepository, AccountRepository accountRepository) {
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
                .map(acc -> getAccountDetails(acc.getAccountId()))
                .toList();
    }

    public GetAccountResponse getAccountDetails(BigInteger accountId) {
        Account account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Account not found: " + accountId)
                );

        try {

            User user = account.getOwner();
            Instant createdAt = account.getCreatedAt();

            IdBatch idBatch = new IdBatch(1);
            idBatch.add(UInt128.asBytes(accountId));
            AccountBatch batch = client.lookupAccounts(idBatch);

            if (batch.getLength() == 0) {
                throw new IllegalStateException("Account not found in TigerBeetle: " + accountId);
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
            throw new IllegalStateException("Failed to get account: " + e.getMessage(), e);
        }
    }
}


