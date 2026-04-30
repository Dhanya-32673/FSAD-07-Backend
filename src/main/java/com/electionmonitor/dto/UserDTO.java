package com.electionmonitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO representing a User profile")
public class UserDTO {
    @Schema(description = "Unique identifier of the user", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Username of the user", example = "johndoe")
    private String username;

    @Schema(description = "Email address", example = "john@example.com")
    private String email;

    @Schema(description = "Full name of the user", example = "John Doe")
    private String fullName;

    @Schema(description = "Role assigned to the user", example = "CITIZEN")
    private String role;

    @Schema(description = "Phone number", example = "+91 9876543210")
    private String phone;

    @Schema(description = "Whether the user account is enabled", example = "true")
    private boolean enabled;

    @Schema(description = "Approval status of the user", example = "APPROVED")
    private String approvalStatus;

    @Schema(description = "Timestamp when the user was created", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
}
