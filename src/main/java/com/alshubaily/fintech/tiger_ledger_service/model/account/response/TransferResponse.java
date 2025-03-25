package com.alshubaily.fintech.tiger_ledger_service.model.account.response;

import java.time.Instant;

public record TransferResponse(
        String transactionId,
        Instant timestamp
) {}
