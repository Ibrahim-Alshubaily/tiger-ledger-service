package com.alshubaily.fintech.tiger_ledger_service.controller;

import com.tigerbeetle.*;
import com.alshubaily.fintech.tiger_ledger_service.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public boolean createAccount(
            @RequestParam long accountId,
            @RequestParam int ledger,
            @RequestParam int code) throws RequestException {
        return accountService.createAccount(accountId, ledger, code);
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
    public boolean transfer(
            @RequestParam long debitAccountId,
            @RequestParam long creditAccountId,
            @RequestParam double amount) throws RequestException {
        return accountService.transfer(debitAccountId, creditAccountId, amount);
    }
}
