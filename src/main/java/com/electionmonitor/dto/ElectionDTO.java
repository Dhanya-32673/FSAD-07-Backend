package com.electionmonitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO representing an Election")
public class ElectionDTO {
    @Schema(description = "Unique identifier of the election", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Schema(description = "Title of the election", example = "General Elections 2024")
    private String title;

    @Schema(description = "Description of the election", example = "National general elections for the parliament")
    private String description;

    @NotBlank(message = "Election type is required")
    @Schema(description = "Type of election", example = "NATIONAL", allowableValues = {"NATIONAL", "STATE", "LOCAL"})
    private String electionType;

    @NotNull(message = "Start date is required")
    @Schema(description = "Start date and time of the election")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @Schema(description = "End date and time of the election")
    private LocalDateTime endDate;

    @Schema(description = "Current status of the election", example = "UPCOMING", allowableValues = {"UPCOMING", "ONGOING", "COMPLETED", "CANCELLED"})
    private String status;

    @Schema(description = "Name of the user who created the election", example = "Admin User", accessMode = Schema.AccessMode.READ_ONLY)
    private String createdByName;

    @Schema(description = "Timestamp when the election was created", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "List of candidates participating in the election")
    private List<CandidateDTO> candidates;

    @Schema(description = "Total number of votes cast", example = "1500", accessMode = Schema.AccessMode.READ_ONLY)
    private long totalVotes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "DTO representing a Candidate")
    public static class CandidateDTO {
        @Schema(description = "Unique identifier of the candidate", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        private Long id;

        @NotBlank(message = "Candidate name is required")
        @Schema(description = "Name of the candidate", example = "John Doe")
        private String name;

        @NotBlank(message = "Party is required")
        @Schema(description = "Political party of the candidate", example = "National Party")
        private String party;

        @Schema(description = "Manifesto of the candidate", example = "I promise to improve education...")
        private String manifesto;

        @Schema(description = "URL to the candidate's photo", example = "http://example.com/photo.jpg")
        private String photoUrl;

        @Schema(description = "Number of votes received by the candidate", example = "500", accessMode = Schema.AccessMode.READ_ONLY)
        private long voteCount;
    }
}
