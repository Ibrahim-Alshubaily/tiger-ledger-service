package com.alshubaily.fintech.tiger_ledger_service.service;

import com.alshubaily.fintech.tiger_ledger_service.db.user.User;
import com.alshubaily.fintech.tiger_ledger_service.db.user.UserRepository;
import com.alshubaily.fintech.tiger_ledger_service.model.Auth.request.LoginRequest;
import com.alshubaily.fintech.tiger_ledger_service.model.Auth.response.LoginResponse;
import com.alshubaily.fintech.tiger_ledger_service.model.Auth.request.SignupRequest;
import com.alshubaily.fintech.tiger_ledger_service.model.Auth.response.SignupResponse;
import com.alshubaily.fintech.tiger_ledger_service.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());

        return new SignupResponse(userRepository.save(user).getId());
    }


    public LoginResponse login(LoginRequest request) {
        Optional<User> optionalUser = Optional.empty();

        if (request.getUsername() != null) {
            optionalUser = userRepository.findByUsername(request.getUsername());
        } else if (request.getEmail() != null) {
            optionalUser = userRepository.findByEmail(request.getEmail());
        }

        User user = optionalUser.orElseThrow(() ->
                new AccessDeniedException("Invalid credentials")
        );

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AccessDeniedException("Invalid credentials");
        }

        return new LoginResponse(jwtUtil.generateToken(user));
    }
}
