package com.alshubaily.fintech.tiger_ledger_service.controller;

import com.alshubaily.fintech.tiger_ledger_service.model.account.response.TransferResponse;
import com.alshubaily.fintech.tiger_ledger_service.model.transaction.request.DepositRequest;
import com.alshubaily.fintech.tiger_ledger_service.model.transaction.request.TransferRequest;
import com.alshubaily.fintech.tiger_ledger_service.model.transaction.request.WithdrawRequest;
import com.alshubaily.fintech.tiger_ledger_service.service.TransactionService;
import jakarta.validation.Valid;
import java.math.BigInteger;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/accounts/{accountId}/transactions")
@PreAuthorize("@authorizationService.hasAccessToAccount(#accountId)")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(
            @PathVariable("accountId") BigInteger accountId,
            @Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(transactionService.transfer(accountId, request));
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransferResponse> deposit(
            @PathVariable("accountId") BigInteger accountId,
            @Valid @RequestBody DepositRequest request) {
        return ResponseEntity.ok(transactionService.deposit(accountId, request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransferResponse> withdraw(
            @PathVariable("accountId") BigInteger accountId,
            @Valid @RequestBody WithdrawRequest request) {
        return ResponseEntity.ok(transactionService.withdraw(accountId, request));
    }
}
