package com.electionmonitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request object for user registration")
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    @Schema(description = "Unique username", example = "johndoe")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Email address", example = "john@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Schema(description = "Password (min 6 chars)", example = "password123")
    private String password;

    @NotBlank(message = "Full name is required")
    @Schema(description = "Full name of the user", example = "John Doe")
    private String fullName;

    @NotBlank(message = "Role is required")
    @Schema(description = "Role for the user", example = "CITIZEN", allowableValues = {"ADMIN", "CITIZEN", "OBSERVER", "ANALYST"})
    private String role;

    @Schema(description = "Phone number of the user", example = "+91 9876543210")
    private String phone;
}
