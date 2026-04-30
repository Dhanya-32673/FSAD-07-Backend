package com.electionmonitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response object containing JWT and user profile")
public class AuthResponse {
    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Username of the authenticated user", example = "admin")
    private String username;

    @Schema(description = "Full name of the user", example = "Vanguard Admin")
    private String fullName;

    @Schema(description = "Email of the user", example = "admin@election.com")
    private String email;

    @Schema(description = "Role assigned to the user", example = "ADMIN")
    private String role;

    @Schema(description = "Unique ID of the user", example = "1")
    private Long userId;

    @Schema(description = "Approval status of the user", example = "APPROVED")
    private String approvalStatus;

    @Schema(description = "True when admin requires OTP verification step")
    private boolean requiresOtp;

    @Schema(description = "Masked email shown during OTP step", example = "d*******e@gmail.com")
    private String maskedEmail;
}
