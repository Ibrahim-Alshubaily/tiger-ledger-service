package com.alshubaily.fintech.tiger_ledger_service.controller;

import com.alshubaily.fintech.tiger_ledger_service.model.Auth.LoginRequest;
import com.alshubaily.fintech.tiger_ledger_service.model.Auth.LoginResponse;
import com.alshubaily.fintech.tiger_ledger_service.model.Auth.SignupRequest;
import com.alshubaily.fintech.tiger_ledger_service.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    @PostMapping("/signup")
    public Long signUp(@RequestBody SignupRequest signupRequest) {
        return authenticationService.signup(signupRequest);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return authenticationService.login(loginRequest);
    }
}
