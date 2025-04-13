package com.alshubaily.fintech.tiger_ledger_service.db.account;

import java.math.BigInteger;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, BigInteger> {
    List<Account> findAllByOwnerId(long userId);
}
