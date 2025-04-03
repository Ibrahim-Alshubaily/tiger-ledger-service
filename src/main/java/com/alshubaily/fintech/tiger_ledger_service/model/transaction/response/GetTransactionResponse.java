package com.alshubaily.fintech.tiger_ledger_service.model.transaction.response;

import com.alshubaily.fintech.tiger_ledger_service.model.transaction.Transaction;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigInteger;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetTransactionResponse(
        Transaction transactionType,
        BigInteger debitAccount,
        BigInteger creditAccount,
        double amount,
        Instant timestamp
) {}
