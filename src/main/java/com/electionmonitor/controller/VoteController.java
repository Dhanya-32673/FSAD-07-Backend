package com.electionmonitor.controller;

import com.electionmonitor.dto.VoteRequest;
import com.electionmonitor.dto.VoteResponseDTO;
import com.electionmonitor.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
@Tag(name = "Voting", description = "Endpoints for casting and checking votes")
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    @Operation(summary = "Cast a vote", description = "Submits a vote for a candidate in an active election")
    @ApiResponse(responseCode = "200", description = "Vote cast successfully")
    @ApiResponse(responseCode = "400", description = "Already voted or election not active")
    public ResponseEntity<VoteResponseDTO> castVote(
            @Valid @RequestBody VoteRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(voteService.castVote(request, authentication.getName()));
    }

    @GetMapping("/check/{electionId}")
    @Operation(summary = "Check if voted", description = "Checks if the current user has already voted in a specific election")
    public ResponseEntity<Map<String, Boolean>> checkVote(
            @PathVariable Long electionId,
            Authentication authentication) {
        boolean hasVoted = voteService.hasUserVoted(electionId, authentication.getName());
        return ResponseEntity.ok(Map.of("hasVoted", hasVoted));
    }

    @GetMapping("/history")
    @Operation(summary = "Get vote history", description = "Retrieves a list of all elections the user has voted in")
    public ResponseEntity<List<VoteResponseDTO>> getVoteHistory(Authentication authentication) {
        return ResponseEntity.ok(voteService.getUserVoteHistory(authentication.getName()));
    }
}
