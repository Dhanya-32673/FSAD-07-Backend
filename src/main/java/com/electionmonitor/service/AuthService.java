package com.electionmonitor.service;

import com.electionmonitor.config.JwtService;
import com.electionmonitor.dto.AuthRequest;
import com.electionmonitor.dto.AuthResponse;
import com.electionmonitor.dto.RegisterRequest;
import com.electionmonitor.model.User;
import com.electionmonitor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final OtpService otpService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User.Role role;
        try {
            role = User.Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + request.getRole());
        }

        
        User.ApprovalStatus approvalStatus =
                (role == User.Role.CITIZEN) ? User.ApprovalStatus.APPROVED : User.ApprovalStatus.PENDING;

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(role)
                .phone(request.getPhone())
                .enabled(true)
                .approvalStatus(approvalStatus)
                .build();

        userRepository.save(user);

        AuthResponse response = modelMapper.map(user, AuthResponse.class);
        response.setRole(user.getRole().name());
        response.setUserId(user.getId());
        response.setApprovalStatus(approvalStatus.name());

        
        if (approvalStatus == User.ApprovalStatus.APPROVED) {
            var userDetails = new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );
            response.setToken(jwtService.generateToken(userDetails));
        }

        return response;
    }

    public AuthResponse login(AuthRequest request) {
        // Catch BadCredentialsException here so it becomes a 400 (via IllegalArgumentException)
        // instead of a 401, which would trigger the frontend's 401 interceptor redirect.
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getIdentifier(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new IllegalArgumentException("Invalid username/email or password.");
        } catch (org.springframework.security.authentication.DisabledException ex) {
            throw new IllegalArgumentException("Your account has been disabled. Contact an administrator.");
        } catch (org.springframework.security.core.AuthenticationException ex) {
            throw new IllegalArgumentException("Authentication failed: " + ex.getMessage());
        }

        User user = userRepository.findByUsername(request.getIdentifier())
                .or(() -> userRepository.findByEmail(request.getIdentifier()))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        
        User.ApprovalStatus approvalStatus = user.getApprovalStatus() != null
                ? user.getApprovalStatus()
                : User.ApprovalStatus.APPROVED;

        
        if (approvalStatus == User.ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("Your account is pending admin approval. Please wait.");
        }
        
        if (approvalStatus == User.ApprovalStatus.REJECTED) {
            throw new IllegalArgumentException("Your access request has been rejected. Contact an administrator.");
        }

        
        if (user.getRole() == User.Role.ADMIN) {
            
            otpService.generateAndSend(user.getUsername(), user.getEmail());

            return AuthResponse.builder()
                    .username(user.getUsername())
                    .requiresOtp(true)
                    .maskedEmail(OtpService.maskEmail(user.getEmail()))
                    .role(user.getRole().name())
                    .approvalStatus(approvalStatus.name())
                    .build();
        }

        
        var userDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );

        String token = jwtService.generateToken(userDetails);

        AuthResponse response = modelMapper.map(user, AuthResponse.class);
        response.setToken(token);
        response.setRole(user.getRole().name());
        response.setUserId(user.getId());
        response.setApprovalStatus(approvalStatus.name());
        return response;
    }

    
    public AuthResponse verifyOtp(String username, String otp) {
        if (!otpService.verify(username, otp)) {
            throw new IllegalArgumentException("Invalid or expired OTP. Please try again.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var userDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );

        String token = jwtService.generateToken(userDetails);

        AuthResponse response = modelMapper.map(user, AuthResponse.class);
        response.setToken(token);
        response.setRole(user.getRole().name());
        response.setUserId(user.getId());
        response.setApprovalStatus(
                user.getApprovalStatus() != null ? user.getApprovalStatus().name() : "APPROVED"
        );
        return response;
    }
}
