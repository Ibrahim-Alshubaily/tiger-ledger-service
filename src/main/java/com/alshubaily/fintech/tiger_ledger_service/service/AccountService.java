package com.alshubaily.fintech.tiger_ledger_service.service;

import com.alshubaily.fintech.tiger_ledger_service.util.CurrencyUtil;
import com.tigerbeetle.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Locale;

@Service
@AllArgsConstructor
public class AccountService {

    private final Client client;

    public boolean createAccount(long accountId, int ledger, int code) throws RequestException {
        AccountBatch accounts = new AccountBatch(1);
        accounts.add();
        accounts.setId(UInt128.asBytes(accountId));
        accounts.setLedger(ledger);
        accounts.setCode(code);
        accounts.setFlags(AccountFlags.NONE);
        CreateAccountResultBatch accountErrors = client.createAccounts(accounts);
        return accountErrors.getLength() == 0;
    }

    public AccountBatch getAccount(long accountId) throws RequestException {
        IdBatch idBatch = new IdBatch(1);
        idBatch.add(UInt128.asBytes(accountId));

        AccountBatch accounts = client.lookupAccounts(idBatch);
        if (accounts.getCapacity() == 0) {
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

    public boolean transfer(long debitAccountId, long creditAccountId, double amountSar) throws RequestException {
        if (amountSar <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero.");
        }

        long amountHalala = CurrencyUtil.sarToHalala(amountSar);

        TransferBatch transfers = new TransferBatch(1);
        transfers.add();
        transfers.setId(UInt128.asBytes(System.nanoTime()));
        transfers.setDebitAccountId(UInt128.asBytes(debitAccountId));
        transfers.setCreditAccountId(UInt128.asBytes(creditAccountId));
        transfers.setAmount(amountHalala);
        transfers.setFlags(TransferFlags.NONE);

        return client.createTransfers(transfers).getLength() == 0;
    }

}
