package com.electionmonitor.controller;

import com.electionmonitor.dto.ElectionDTO;
import com.electionmonitor.service.ElectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/elections")
@RequiredArgsConstructor
@Tag(name = "Election Management", description = "Endpoints for managing elections and candidates")
public class ElectionController {

    private final ElectionService electionService;

    @GetMapping
    @Operation(summary = "Get all elections", description = "Retrieves a list of all elections ordered by creation date")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    public ResponseEntity<List<ElectionDTO>> getAllElections() {
        return ResponseEntity.ok(electionService.getAllElections());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get election by ID", description = "Retrieves details of a specific election")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved election")
    @ApiResponse(responseCode = "404", description = "Election not found")
    public ResponseEntity<ElectionDTO> getElectionById(@PathVariable Long id) {
        return ResponseEntity.ok(electionService.getElectionById(id));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get elections by status", description = "Retrieves elections filtered by status (UPCOMING, ONGOING, etc.)")
    public ResponseEntity<List<ElectionDTO>> getElectionsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(electionService.getElectionsByStatus(status));
    }

    @PostMapping
    @Operation(summary = "Create an election", description = "Initializes a new election (Admin only)")
    @ApiResponse(responseCode = "200", description = "Successfully created election")
    public ResponseEntity<ElectionDTO> createElection(
            @Valid @RequestBody ElectionDTO dto,
            Authentication authentication) {
        return ResponseEntity.ok(electionService.createElection(dto, authentication.getName()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an election", description = "Updates details or status of an existing election")
    @ApiResponse(responseCode = "200", description = "Successfully updated")
    @ApiResponse(responseCode = "404", description = "Election not found")
    public ResponseEntity<ElectionDTO> updateElection(
            @PathVariable Long id,
            @Valid @RequestBody ElectionDTO dto) {
        return ResponseEntity.ok(electionService.updateElection(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an election", description = "Permanently removes an election")
    @ApiResponse(responseCode = "204", description = "Successfully deleted")
    public ResponseEntity<Void> deleteElection(@PathVariable Long id) {
        electionService.deleteElection(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{electionId}/candidates")
    @Operation(summary = "Add candidate to election", description = "Adds a candidate to a specific election")
    @ApiResponse(responseCode = "200", description = "Successfully added candidate")
    public ResponseEntity<ElectionDTO.CandidateDTO> addCandidate(
            @PathVariable Long electionId,
            @Valid @RequestBody ElectionDTO.CandidateDTO dto) {
        return ResponseEntity.ok(electionService.addCandidate(electionId, dto));
    }
}
