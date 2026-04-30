package com.electionmonitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO representing an Incident report")
public class IncidentDTO {
    @Schema(description = "Unique identifier of the incident", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Schema(description = "Title of the incident", example = "Voter intimidation at Booth 5")
    private String title;

    @NotBlank(message = "Description is required")
    @Schema(description = "Detailed description of the incident", example = "A group of people were seen harassing voters near the entrance.")
    private String description;

    @NotBlank(message = "Category is required")
    @Schema(description = "Category of the incident", example = "VIOLENCE", allowableValues = {"VIOLENCE", "FRAUD", "TECHNICAL_ISSUE", "PROCEDURAL_ERROR", "OTHER"})
    private String category;

    @NotBlank(message = "Severity is required")
    @Schema(description = "Severity level of the incident", example = "HIGH", allowableValues = {"LOW", "MEDIUM", "HIGH", "CRITICAL"})
    private String severity;

    @Schema(description = "Current status of the incident", example = "REPORTED", allowableValues = {"REPORTED", "INVESTIGATING", "RESOLVED", "DISMISSED"})
    private String status;

    @Schema(description = "Location where the incident occurred", example = "Downtown High School")
    private String location;

    @NotNull(message = "Election ID is required")
    @Schema(description = "ID of the election related to this incident", example = "1")
    private Long electionId;

    @Schema(description = "Title of the election", example = "General Elections 2024", accessMode = Schema.AccessMode.READ_ONLY)
    private String electionTitle;

    @Schema(description = "Name of the user who reported the incident", example = "John Citizen", accessMode = Schema.AccessMode.READ_ONLY)
    private String reportedByName;

    @Schema(description = "ID of the user who reported the incident", accessMode = Schema.AccessMode.READ_ONLY)
    private Long reportedById;

    @Schema(description = "Timestamp when the incident was reported", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime reportedAt;

    @Schema(description = "Timestamp when the incident was resolved", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime resolvedAt;
}
