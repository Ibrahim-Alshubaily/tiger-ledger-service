package com.alshubaily.fintech.tiger_ledger_service.model.account.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import java.math.BigInteger;

public record WithdrawRequest(
        @Min(1) BigInteger accountId,
        @Positive double amount
) {}
