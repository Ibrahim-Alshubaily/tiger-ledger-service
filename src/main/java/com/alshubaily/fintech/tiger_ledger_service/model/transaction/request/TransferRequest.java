package com.alshubaily.fintech.tiger_ledger_service.model.transaction.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import java.math.BigInteger;

public record TransferRequest(

        @Min(value = 1, message = "Credit account ID must be a positive integer")
        BigInteger creditAccountId,

        @Positive(message = "Transfer amount must be positive")
        double amount
) {}
