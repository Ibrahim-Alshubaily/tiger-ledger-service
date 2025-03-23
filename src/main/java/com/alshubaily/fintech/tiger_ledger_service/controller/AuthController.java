package com.alshubaily.fintech.tiger_ledger_service.controller;

import com.alshubaily.fintech.tiger_ledger_service.model.Auth.LoginRequest;
import com.alshubaily.fintech.tiger_ledger_service.model.Auth.LoginResponse;
import com.alshubaily.fintech.tiger_ledger_service.model.Auth.SignupRequest;
import com.alshubaily.fintech.tiger_ledger_service.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    @PostMapping("/signup")
    public Long signUp(@RequestBody SignupRequest signupRequest) {
        return authService.signup(signupRequest);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}
