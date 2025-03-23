package com.alshubaily.fintech.tiger_ledger_service.model.Auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    private String email;
}
