package com.electionmonitor.controller;

import com.electionmonitor.dto.AnalyticsDTO;
import com.electionmonitor.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Endpoints for retrieving system-wide analytics and statistics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping
    @Operation(summary = "Get system analytics", description = "Retrieves an overview of elections, voters, and incident statistics")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved analytics")
    public ResponseEntity<AnalyticsDTO> getAnalytics() {
        return ResponseEntity.ok(analyticsService.getOverviewAnalytics());
    }
}
