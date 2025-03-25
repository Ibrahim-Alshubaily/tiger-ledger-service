package com.alshubaily.fintech.tiger_ledger_service.db.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, BigInteger> {
    List<Account> findAllByOwnerId(long userId);
}
