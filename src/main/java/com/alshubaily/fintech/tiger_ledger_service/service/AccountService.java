package com.alshubaily.fintech.tiger_ledger_service.service;

import com.tigerbeetle.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
}
