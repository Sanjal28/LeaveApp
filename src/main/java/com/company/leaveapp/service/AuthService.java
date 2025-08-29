// src/main/java/com/company/leaveapp/service/AuthService.java
package com.company.leaveapp.service;

import com.company.leaveapp.dto.AuthRequest;
import com.company.leaveapp.dto.AuthResponse;
import com.company.leaveapp.dto.RegisterRequest;
import com.company.leaveapp.models.Role;
import com.company.leaveapp.models.User;
import com.company.leaveapp.models.UserStatus;
import com.company.leaveapp.repository.UserRepository;
import com.company.leaveapp.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        var user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role() != null ? request.role() : Role.EMPLOYEE) // Default role
                .userStatus(UserStatus.ACTIVE) // Or PENDING for activation flow
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken, user.getName(), user.getRole(), user.getUserStatus());
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(); // Should not happen if auth is successful
        var jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken, user.getName(), user.getRole(), user.getUserStatus());
    }
}