package com.electionmonitor.controller;

import com.electionmonitor.dto.AuthRequest;
import com.electionmonitor.dto.AuthResponse;
import com.electionmonitor.dto.RegisterRequest;
import com.electionmonitor.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;
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
    @Operation(summary = "Login — step 1: validate credentials",
               description = "For ADMIN: returns requiresOtp=true and sends OTP. For others: returns JWT directly.")
    @ApiResponse(responseCode = "200", description = "Credentials valid")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Login — step 2 (Admin only): verify OTP",
               description = "Validates the OTP sent to admin email and returns JWT token.")
    @ApiResponse(responseCode = "200", description = "OTP valid — JWT returned")
    @ApiResponse(responseCode = "400", description = "Invalid or expired OTP")
    public ResponseEntity<AuthResponse> verifyOtp(@RequestBody OtpVerifyRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request.getUsername(), request.getOtp()));
    }

    @Data
    public static class OtpVerifyRequest {
        private String username;
        private String otp;
    }
}
