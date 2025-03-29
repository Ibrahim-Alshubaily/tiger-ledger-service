package com.alshubaily.fintech.tiger_ledger_service.model.Auth.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {
    private String username;
    private String password;
    private String email;
}
