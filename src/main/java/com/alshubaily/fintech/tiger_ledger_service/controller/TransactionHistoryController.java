package com.alshubaily.fintech.tiger_ledger_service.controller;

import com.alshubaily.fintech.tiger_ledger_service.model.transaction.response.GetTransactionResponse;
import com.alshubaily.fintech.tiger_ledger_service.service.TransactionHistoryService;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.List;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/accounts/{accountId}/transactions")
@PreAuthorize("@authorizationService.hasAccessToAccount(#accountId)")
public class TransactionHistoryController {
    private final TransactionHistoryService transactionHistoryService;
    @GetMapping
    public ResponseEntity<List<GetTransactionResponse>> getTransactionHistory(@PathVariable @Min(1) BigInteger accountId) {
        return ResponseEntity.ok(transactionHistoryService.getTransactionHistory(accountId));
    }
}
