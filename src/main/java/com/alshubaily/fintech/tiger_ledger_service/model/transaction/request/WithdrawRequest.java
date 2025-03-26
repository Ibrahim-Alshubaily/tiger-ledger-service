package com.alshubaily.fintech.tiger_ledger_service.model.transaction.request;

import jakarta.validation.constraints.Positive;

public record WithdrawRequest(

        @Positive(message = "Withdraw amount must be positive")
        double amount
) {}
