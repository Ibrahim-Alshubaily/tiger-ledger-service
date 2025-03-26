package com.alshubaily.fintech.tiger_ledger_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class HealthCheckController {
    @GetMapping("/api/v1/health")
    public HealthResponse checkHealth() {
        return new HealthResponse("OK", Instant.now());
    }
    record HealthResponse(String status, Instant timestamp) {}
}
