package com.alshubaily.fintech.tiger_ledger_service.model.transaction.request;

import jakarta.validation.constraints.Positive;

public record DepositRequest(
        @Positive(message = "Deposit amount must be positive") double amount) {}
