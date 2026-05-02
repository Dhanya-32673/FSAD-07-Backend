package com.electionmonitor.controller;

import com.electionmonitor.dto.AuthRequest;
import com.electionmonitor.dto.AuthResponse;
import com.electionmonitor.dto.RegisterRequest;
import com.electionmonitor.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @ApiResponse(responseCode = "200", description = "Successfully registered")
    @ApiResponse(responseCode = "400", description = "Invalid input or user already exists")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login — validate credentials and return JWT")
    @ApiResponse(responseCode = "200", description = "Credentials valid — JWT returned")
    @ApiResponse(responseCode = "400", description = "Invalid credentials")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
