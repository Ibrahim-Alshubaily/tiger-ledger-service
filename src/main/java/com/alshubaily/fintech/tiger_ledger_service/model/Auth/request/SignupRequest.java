package com.alshubaily.fintech.tiger_ledger_service.model.Auth.request;

import lombok.Data;
@Data
public class SignupRequest {
    private String username;
    private String password;
    private String email;
}
