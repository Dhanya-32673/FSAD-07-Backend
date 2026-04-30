package com.electionmonitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO representing high-level system analytics")
public class AnalyticsDTO {

    
    @Schema(description = "Total number of elections in the system", example = "10")
    private long totalElections;

    @Schema(description = "Number of active/ongoing elections", example = "3")
    private long activeElections;

    @Schema(description = "Total number of registered voters", example = "5000")
    private long totalVoters;

    @Schema(description = "Total number of votes cast across all elections", example = "15000")
    private long totalVotesCast;

    @Schema(description = "Overall voter turnout percentage", example = "75.5")
    private double overallTurnoutPercentage;

    @Schema(description = "Total number of reported incidents", example = "45")
    private long totalIncidents;

    @Schema(description = "Total number of resolved incidents", example = "30")
    private long resolvedIncidents;

    @Schema(description = "Total number of pending incidents", example = "15")
    private long pendingIncidents;

    
    @Schema(description = "Analytics for individual elections")
    private List<ElectionAnalytics> electionAnalytics;

    @Schema(description = "Count of incidents grouped by category")
    private Map<String, Long> incidentsByCategory;

    @Schema(description = "Count of incidents grouped by severity")
    private Map<String, Long> incidentsBySeverity;

    @Schema(description = "Count of users grouped by their role")
    private Map<String, Long> usersByRole;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Analytics for a specific election")
    public static class ElectionAnalytics {
        @Schema(description = "ID of the election", example = "1")
        private Long electionId;

        @Schema(description = "Title of the election", example = "General Elections 2024")
        private String electionTitle;

        @Schema(description = "Status of the election", example = "ONGOING")
        private String status;

        @Schema(description = "Total votes cast in this election", example = "1200")
        private long totalVotes;

        @Schema(description = "Total number of candidates in this election", example = "5")
        private long totalCandidates;

        @Schema(description = "Total incidents reported for this election", example = "8")
        private long totalIncidents;

        @Schema(description = "Vote breakdown for each candidate")
        private List<CandidateVoteCount> candidateVotes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Vote count for a specific candidate")
    public static class CandidateVoteCount {
        @Schema(description = "ID of the candidate", example = "1")
        private Long candidateId;

        @Schema(description = "Name of the candidate", example = "John Doe")
        private String candidateName;

        @Schema(description = "Party of the candidate", example = "National Party")
        private String party;

        @Schema(description = "Number of votes received", example = "400")
        private long voteCount;

        @Schema(description = "Percentage of total votes received", example = "33.33")
        private double percentage;
    }
}
