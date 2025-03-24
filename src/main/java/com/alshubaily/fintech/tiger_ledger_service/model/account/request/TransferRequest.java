package com.alshubaily.fintech.tiger_ledger_service.model.account.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public record TransferRequest(
        @Min(1) long debitAccountId,
        @Min(1) long creditAccountId,
        @Positive double amount
) {}
