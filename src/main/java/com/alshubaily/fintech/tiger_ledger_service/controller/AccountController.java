package com.alshubaily.fintech.tiger_ledger_service.controller;

import com.alshubaily.fintech.tiger_ledger_service.model.Transaction;
import com.tigerbeetle.*;
import com.alshubaily.fintech.tiger_ledger_service.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/accounts")
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public boolean createAccount(@RequestParam long accountId) throws RequestException {
        return accountService.createAccount(accountId);
    }

    @GetMapping("/{accountId}")
    public AccountBatch getAccount(@PathVariable long accountId) throws RequestException {
        return accountService.getAccount(accountId);
    }

    @GetMapping("/{accountId}/balance")
    public String getAccountBalance(@PathVariable long accountId) throws RequestException {
        return accountService.getAccountBalance(accountId);
    }

    @PostMapping("/transfer")
    public CompletableFuture<ResponseEntity<Boolean>> transfer(
            @RequestParam long debitAccountId,
            @RequestParam long creditAccountId,
            @RequestParam double amount) {

        return accountService.transfer(debitAccountId, creditAccountId, amount)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/deposit")
    public CompletableFuture<ResponseEntity<Boolean>> deposit(
            @RequestParam long accountId,
            @RequestParam double amount) {

        return accountService.deposit(accountId, amount)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/withdraw")
    public CompletableFuture<ResponseEntity<Boolean>> withdraw(
            @RequestParam long accountId,
            @RequestParam double amount) {

        return accountService.withdraw(accountId, amount)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<Transaction>> getTransactionHistory(
            @PathVariable long accountId) {
        return ResponseEntity.ok(accountService.getTransactionHistory(accountId));
    }
}
