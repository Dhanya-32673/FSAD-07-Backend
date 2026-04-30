package com.electionmonitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request object for casting a vote")
public class VoteRequest {
    @NotNull(message = "Election ID is required")
    @Schema(description = "ID of the election", example = "1")
    private Long electionId;

    @NotNull(message = "Candidate ID is required")
    @Schema(description = "ID of the candidate to vote for", example = "2")
    private Long candidateId;
}
