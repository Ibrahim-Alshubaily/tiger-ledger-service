package com.alshubaily.fintech.tiger_ledger_service.service;

import com.alshubaily.fintech.tiger_ledger_service.util.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@AllArgsConstructor
@Component("authorizationService")
public class AuthorizationService {

    private final SecurityUtil securityUtil;
    private final AccountOwnerCacheService accountOwnerCacheService;

    public boolean hasAccessToAccount(BigInteger accountId) {
        long currentUserId = securityUtil.getAuthenticatedUserId();
        return accountOwnerCacheService.getAccountOwnerId(accountId).equals(currentUserId);
    }
}

