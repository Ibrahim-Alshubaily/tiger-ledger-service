package com.alshubaily.fintech.tiger_ledger_service.eventbus;



import com.alshubaily.fintech.tiger_ledger_service.model.transaction.TransactionType;

import java.math.BigInteger;
import java.sql.Timestamp;

public record TransactionEvent(
        BigInteger id,
        BigInteger debitAccountId,
        BigInteger creditAccountId,
        BigInteger amount,
        BigInteger pendingId,
        BigInteger userData128,
        long userData64,
        int userData32,
        int timeout,
        int ledger,
        int code,
        int flags,
        Timestamp timestamp,
        TransactionType transactionType
) {}
