package com.electionmonitor.controller;

import com.electionmonitor.dto.IncidentDTO;
import com.electionmonitor.service.IncidentService;
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
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
@Tag(name = "Incident Reporting", description = "Endpoints for reporting and managing election incidents")
public class IncidentController {

    private final IncidentService incidentService;

    @GetMapping
    @Operation(summary = "Get all incidents", description = "Retrieves all reported incidents across all elections")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    public ResponseEntity<List<IncidentDTO>> getAllIncidents() {
        return ResponseEntity.ok(incidentService.getAllIncidents());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get incident by ID", description = "Retrieves details of a specific incident")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved incident")
    @ApiResponse(responseCode = "404", description = "Incident not found")
    public ResponseEntity<IncidentDTO> getIncidentById(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getIncidentById(id));
    }

    @GetMapping("/election/{electionId}")
    @Operation(summary = "Get incidents by election", description = "Retrieves all incidents associated with a specific election")
    public ResponseEntity<List<IncidentDTO>> getIncidentsByElection(@PathVariable Long electionId) {
        return ResponseEntity.ok(incidentService.getIncidentsByElection(electionId));
    }

    @GetMapping("/my-reports")
    @Operation(summary = "Get my reported incidents", description = "Retrieves incidents reported by the currently authenticated user")
    public ResponseEntity<List<IncidentDTO>> getMyIncidents(Authentication authentication) {
        return ResponseEntity.ok(incidentService.getIncidentsByUser(authentication.getName()));
    }

    @PostMapping
    @Operation(summary = "Report an incident", description = "Creates a new incident report")
    @ApiResponse(responseCode = "200", description = "Successfully reported")
    public ResponseEntity<IncidentDTO> createIncident(
            @Valid @RequestBody IncidentDTO dto,
            Authentication authentication) {
        return ResponseEntity.ok(incidentService.createIncident(dto, authentication.getName()));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update incident status", description = "Updates the status of an incident (e.g., INVESTIGATING, RESOLVED)")
    @ApiResponse(responseCode = "200", description = "Successfully updated status")
    public ResponseEntity<IncidentDTO> updateIncidentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(incidentService.updateIncidentStatus(id, body.get("status")));
    }
}
