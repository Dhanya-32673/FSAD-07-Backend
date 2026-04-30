package com.electionmonitor.service;

import com.electionmonitor.dto.AnalyticsDTO;
import com.electionmonitor.model.Election;
import com.electionmonitor.model.Incident;
import com.electionmonitor.model.User;
import com.electionmonitor.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ElectionRepository electionRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final IncidentRepository incidentRepository;
    private final CandidateRepository candidateRepository;

    @Transactional(readOnly = true)
    public AnalyticsDTO getOverviewAnalytics() {
        long totalElections = electionRepository.count();
        long activeElections = electionRepository.countByStatus(Election.ElectionStatus.ACTIVE);
        long totalVoters = userRepository.countByRole(User.Role.CITIZEN);
        long totalVotesCast = voteRepository.count();
        double turnout = totalVoters > 0 ? (double) totalVotesCast / totalVoters * 100 : 0;
        long totalIncidents = incidentRepository.count();
        long resolvedIncidents = incidentRepository.countByStatus(Incident.IncidentStatus.RESOLVED);
        long pendingIncidents = incidentRepository.countByStatus(Incident.IncidentStatus.REPORTED)
                + incidentRepository.countByStatus(Incident.IncidentStatus.UNDER_REVIEW);

        
        Map<String, Long> incidentsByCategory = new LinkedHashMap<>();
        for (Incident.IncidentCategory category : Incident.IncidentCategory.values()) {
            incidentsByCategory.put(category.name(), incidentRepository.countByCategory(category));
        }

        
        Map<String, Long> incidentsBySeverity = new LinkedHashMap<>();
        for (Incident.Severity severity : Incident.Severity.values()) {
            incidentsBySeverity.put(severity.name(), incidentRepository.countBySeverity(severity));
        }

        
        Map<String, Long> usersByRole = new LinkedHashMap<>();
        for (User.Role role : User.Role.values()) {
            usersByRole.put(role.name(), userRepository.countByRole(role));
        }

        
        List<AnalyticsDTO.ElectionAnalytics> electionAnalyticsList = electionRepository.findAll()
                .stream()
                .map(election -> {
                    long electionVotes = voteRepository.countByElectionId(election.getId());
                    long electionCandidates = candidateRepository.findByElectionId(election.getId()).size();
                    long electionIncidents = incidentRepository.countByElectionId(election.getId());

                    List<Object[]> voteCounts = voteRepository.getVoteCountsByElection(election.getId());
                    List<AnalyticsDTO.CandidateVoteCount> candidateVotes = voteCounts.stream()
                            .map(row -> AnalyticsDTO.CandidateVoteCount.builder()
                                    .candidateId((Long) row[0])
                                    .candidateName((String) row[1])
                                    .party((String) row[2])
                                    .voteCount((Long) row[3])
                                    .percentage(electionVotes > 0 ? (double) (Long) row[3] / electionVotes * 100 : 0)
                                    .build())
                            .collect(Collectors.toList());

                    return AnalyticsDTO.ElectionAnalytics.builder()
                            .electionId(election.getId())
                            .electionTitle(election.getTitle())
                            .status(election.getStatus().name())
                            .totalVotes(electionVotes)
                            .totalCandidates(electionCandidates)
                            .totalIncidents(electionIncidents)
                            .candidateVotes(candidateVotes)
                            .build();
                })
                .collect(Collectors.toList());

        return AnalyticsDTO.builder()
                .totalElections(totalElections)
                .activeElections(activeElections)
                .totalVoters(totalVoters)
                .totalVotesCast(totalVotesCast)
                .overallTurnoutPercentage(Math.round(turnout * 100.0) / 100.0)
                .totalIncidents(totalIncidents)
                .resolvedIncidents(resolvedIncidents)
                .pendingIncidents(pendingIncidents)
                .electionAnalytics(electionAnalyticsList)
                .incidentsByCategory(incidentsByCategory)
                .incidentsBySeverity(incidentsBySeverity)
                .usersByRole(usersByRole)
                .build();
    }
}
