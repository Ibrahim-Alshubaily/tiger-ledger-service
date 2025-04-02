package com.alshubaily.fintech.tiger_ledger_service.model.account.response;


import java.time.Instant;
import java.math.BigInteger;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetAccountResponse(
        BigInteger accountId,
        String Balance,
        Double balanceSar,
        Instant createdAt,
        Long ownerId,
        String ownerName
) {}

