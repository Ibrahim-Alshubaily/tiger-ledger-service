package com.alshubaily.fintech.tiger_ledger_service.service;

import com.alshubaily.fintech.tiger_ledger_service.db.user.User;
import com.alshubaily.fintech.tiger_ledger_service.db.user.UserRepository;
import com.alshubaily.fintech.tiger_ledger_service.model.Auth.LoginRequest;
import com.alshubaily.fintech.tiger_ledger_service.model.Auth.LoginResponse;
import com.alshubaily.fintech.tiger_ledger_service.model.Auth.SignupRequest;
import com.alshubaily.fintech.tiger_ledger_service.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private final JwtUtil jwtUtil;

    public Long signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());

        return userRepository.save(user).getId();
    }


    public LoginResponse login(LoginRequest request) {
        Optional<User> optionalUser = Optional.empty();

        if (request.getUsername() != null) {
            optionalUser = userRepository.findByUsername(request.getUsername());
        } else if (request.getEmail() != null) {
            optionalUser = userRepository.findByEmail(request.getEmail());
        }

        User user = optionalUser.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")
        );

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return new LoginResponse(jwtUtil.generateToken(user));
    }
}
