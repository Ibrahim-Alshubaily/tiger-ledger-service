package com.alshubaily.fintech.tiger_ledger_service.service;

import com.alshubaily.fintech.tiger_ledger_service.db.account.Account;
import com.alshubaily.fintech.tiger_ledger_service.db.account.AccountRepository;
import com.alshubaily.fintech.tiger_ledger_service.util.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;

@AllArgsConstructor
@Component("authorizationService")
public class AuthorizationService {

    private final SecurityUtil securityUtil;
    private final AccountRepository accountRepository;

    public boolean hasAccessToAccount(BigInteger accountId) {
        long currentUserId = securityUtil.getAuthenticatedUserId();
        Account account = accountRepository.findById(accountId).orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found: " + accountId));
        return account.getOwner().getId().equals(currentUserId);
    }
}

