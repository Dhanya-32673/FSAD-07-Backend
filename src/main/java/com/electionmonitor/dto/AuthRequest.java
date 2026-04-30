package com.electionmonitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication request for login")
public class AuthRequest {
    @NotBlank(message = "Email or Username is required")
    @Schema(description = "Username or Email address", example = "admin@election.com")
    private String identifier;

    @NotBlank(message = "Password is required")
    @Schema(description = "Password", example = "password123")
    private String password;
}
