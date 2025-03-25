package com.alshubaily.fintech.tiger_ledger_service.db.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface AccountRepository extends JpaRepository<Account, BigInteger> {
}
