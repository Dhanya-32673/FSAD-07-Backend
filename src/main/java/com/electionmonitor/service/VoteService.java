package com.electionmonitor.service;

import com.electionmonitor.dto.VoteRequest;
import com.electionmonitor.dto.VoteResponseDTO;
import com.electionmonitor.exception.ResourceNotFoundException;
import com.electionmonitor.model.Candidate;
import com.electionmonitor.model.Election;
import com.electionmonitor.model.User;
import com.electionmonitor.model.Vote;
import com.electionmonitor.repository.CandidateRepository;
import com.electionmonitor.repository.ElectionRepository;
import com.electionmonitor.repository.UserRepository;
import com.electionmonitor.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;

    @Transactional
    public VoteResponseDTO castVote(VoteRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Election election = electionRepository.findById(request.getElectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Election not found"));

        if (election.getStatus() != Election.ElectionStatus.ACTIVE) {
            throw new IllegalArgumentException("Election is not currently active");
        }

        if (voteRepository.existsByElectionIdAndUserId(request.getElectionId(), user.getId())) {
            throw new IllegalArgumentException("You have already voted in this election");
        }

        Candidate candidate = candidateRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        if (!candidate.getElection().getId().equals(election.getId())) {
            throw new IllegalArgumentException("Candidate does not belong to this election");
        }

        Vote vote = Vote.builder()
                .election(election)
                .candidate(candidate)
                .user(user)
                .build();

        voteRepository.save(vote);

        return VoteResponseDTO.builder()
                .message("Vote cast successfully")
                .electionId(election.getId())
                .electionTitle(election.getTitle())
                .candidateName(candidate.getName())
                .candidateParty(candidate.getParty())
                .build();
    }

    public boolean hasUserVoted(Long electionId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return voteRepository.existsByElectionIdAndUserId(electionId, user.getId());
    }

    public List<VoteResponseDTO> getUserVoteHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return voteRepository.findByUserId(user.getId()).stream().map(vote -> 
            VoteResponseDTO.builder()
                .electionId(vote.getElection().getId())
                .electionTitle(vote.getElection().getTitle())
                .candidateName(vote.getCandidate().getName())
                .candidateParty(vote.getCandidate().getParty())
                .votedAt(vote.getVotedAt())
                .build()
        ).collect(Collectors.toList());
    }
}
