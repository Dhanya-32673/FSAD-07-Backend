package com.electionmonitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO representing a vote record")
public class VoteResponseDTO {
    @Schema(description = "ID of the election", example = "1")
    private Long electionId;

    @Schema(description = "Title of the election", example = "General Elections 2024")
    private String electionTitle;

    @Schema(description = "Name of the candidate voted for", example = "John Doe")
    private String candidateName;

    @Schema(description = "Party of the candidate", example = "National Party")
    private String candidateParty;

    @Schema(description = "Timestamp when the vote was cast")
    private LocalDateTime votedAt;

    @Schema(description = "Success message", example = "Vote cast successfully")
    private String message;
}
