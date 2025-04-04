package com.alshubaily.fintech.tiger_ledger_service.util;

import com.alshubaily.fintech.tiger_ledger_service.db.account.Account;
import com.alshubaily.fintech.tiger_ledger_service.db.account.AccountRepository;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
@AllArgsConstructor
public class SecurityUtil {

    public static long getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Unauthenticated");
        }

        if (auth.getCredentials() instanceof Claims claims) {
            if (claims.get("userId") instanceof Number number) {
                return number.longValue();
            }
        }
        throw new IllegalStateException("userId claim not found in authentication claims");
    }
}
