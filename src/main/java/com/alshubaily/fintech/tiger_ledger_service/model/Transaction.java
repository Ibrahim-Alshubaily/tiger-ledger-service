package com.alshubaily.fintech.tiger_ledger_service.model;

import java.time.Instant;

public record Transaction(
        long debitAccount,
        long creditAccount,
        double amount,
        Instant timestamp
) {}
