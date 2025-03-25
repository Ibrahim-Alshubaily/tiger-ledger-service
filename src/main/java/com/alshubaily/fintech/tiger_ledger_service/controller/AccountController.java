package com.alshubaily.fintech.tiger_ledger_service.controller;

import com.alshubaily.fintech.tiger_ledger_service.model.Transaction;

import com.alshubaily.fintech.tiger_ledger_service.model.account.request.*;
import com.alshubaily.fintech.tiger_ledger_service.model.account.response.*;
import com.tigerbeetle.*;
import com.alshubaily.fintech.tiger_ledger_service.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/accounts")
@AllArgsConstructor
@Validated
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
    public AccountBatch getAccount(@PathVariable @Min(1) BigInteger accountId) throws RequestException {
        return accountService.getAccount(accountId);
    }

    @GetMapping("/{accountId}/balance")
    public String getAccountBalance(@PathVariable @Min(1) BigInteger accountId) throws RequestException {
        return accountService.getAccountBalance(accountId);
    }

    @PostMapping("/transfer")
    public CompletableFuture<ResponseEntity<Boolean>> transfer(@Valid @RequestBody TransferRequest request) {
        return accountService.transfer(request)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/deposit")
    public CompletableFuture<ResponseEntity<Boolean>> deposit(@Valid @RequestBody DepositRequest request) {
        return accountService.deposit(request)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/withdraw")
    public CompletableFuture<ResponseEntity<Boolean>> withdraw(@Valid @RequestBody WithdrawRequest request) {
        return accountService.withdraw(request)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<Transaction>> getTransactionHistory(@PathVariable @Min(1) long accountId) {
        return ResponseEntity.ok(accountService.getTransactionHistory(accountId));
    }
}
