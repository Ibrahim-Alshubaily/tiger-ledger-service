package com.alshubaily.fintech.tiger_ledger_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthCheckController {
    @GetMapping("/health")
    public String healthCheck() {
        return "Tiger Ledger Service is up!";
    }
}
