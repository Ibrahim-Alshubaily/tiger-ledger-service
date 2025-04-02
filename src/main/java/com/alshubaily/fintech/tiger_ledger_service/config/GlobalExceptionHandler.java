package com.alshubaily.fintech.tiger_ledger_service.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleExceptions(Exception e) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", e.getMessage());
        body.put("timestamp", Instant.now());
        return ResponseEntity.status(getErrorCode(e)).body(body);
    }

    private HttpStatus getErrorCode(Exception e) {

        if (e instanceof ResponseStatusException ex
                && ex.getStatusCode() instanceof HttpStatus status) {
            return status;
        }

        if (e instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST;
        }

        if (e instanceof AccessDeniedException) {
            return HttpStatus.UNAUTHORIZED;
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
