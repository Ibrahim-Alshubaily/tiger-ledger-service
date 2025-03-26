package com.alshubaily.fintech.tiger_ledger_service.model.transaction.response;

import java.time.Instant;

public record GetTransactionResponse(
        long debitAccount,
        long creditAccount,
        double amount,
        Instant timestamp
) {}
