package com.alshubaily.fintech.tiger_ledger_service.model.account.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigInteger;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetAccountResponse(
        BigInteger accountId,
        String Balance,
        Double balanceSar,
        Instant createdAt,
        Long ownerId,
        String ownerName) {}
