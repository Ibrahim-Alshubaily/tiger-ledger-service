package com.alshubaily.fintech.tiger_ledger_service.controller;

import com.alshubaily.fintech.tiger_ledger_service.model.Transaction;

import com.alshubaily.fintech.tiger_ledger_service.model.account.request.*;
import com.alshubaily.fintech.tiger_ledger_service.model.account.response.*;
import com.tigerbeetle.*;
import com.alshubaily.fintech.tiger_ledger_service.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.math.BigInteger;
import java.util.List;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<CreateAccountResponse> createAccount() throws Exception {
        return ResponseEntity.ok(accountService.createAccount());
    }

    @GetMapping
    public ResponseEntity<List<GetAccountResponse>> getAccounts() {
        return ResponseEntity.ok(accountService.getAccounts());
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("@authorizationService.hasAccessToAccount(#accountId)")
    public ResponseEntity<GetAccountResponse> getAccount(@PathVariable @Min(1) BigInteger accountId) throws RequestException {
        return ResponseEntity.ok(accountService.getAccountDetails(accountId));
    }

    @PostMapping("/transfer")
    @PreAuthorize("@authorizationService.hasAccessToAccount(#request.debitAccountId)")
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(accountService.transfer(request));
    }

    @PostMapping("/deposit")
    @PreAuthorize("@authorizationService.hasAccessToAccount(#request.accountId)")
    public ResponseEntity<TransferResponse> deposit(@Valid @RequestBody DepositRequest request) {
        return ResponseEntity.ok(accountService.deposit(request));

    }

    @PostMapping("/withdraw")
    @PreAuthorize("@authorizationService.hasAccessToAccount(#request.accountId)")
    public ResponseEntity<TransferResponse> withdraw(@Valid @RequestBody WithdrawRequest request) {
        return ResponseEntity.ok(accountService.withdraw(request));

    }

    @GetMapping("/{accountId}/transactions")
    @PreAuthorize("@authorizationService.hasAccessToAccount(#accountId)")
    public ResponseEntity<List<Transaction>> getTransactionHistory(@PathVariable @Min(1) long accountId) {
        return ResponseEntity.ok(accountService.getTransactionHistory(accountId));
    }
}
