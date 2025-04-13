package com.alshubaily.fintech.tiger_ledger_service.service;

import com.alshubaily.fintech.tiger_ledger_service.db.account.AccountRepository;
import java.math.BigInteger;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class AccountOwnerCacheService {

    private final AccountRepository accountRepository;

    @Cacheable(value = "accountOwners", key = "#accountId")
    public Long getAccountOwnerId(BigInteger accountId) {
        return accountRepository
                .findById(accountId)
                .map(account -> account.getOwner().getId())
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Account not found"));
    }
}
