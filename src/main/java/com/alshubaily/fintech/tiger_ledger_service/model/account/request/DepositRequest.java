package com.alshubaily.fintech.tiger_ledger_service.model.account.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public record DepositRequest(
        @Min(1) long accountId,
        @Positive double amount
) {}
