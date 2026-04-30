package com.electionmonitor.service;

import com.electionmonitor.dto.ElectionDTO;
import com.electionmonitor.exception.ResourceNotFoundException;
import com.electionmonitor.model.Candidate;
import com.electionmonitor.model.Election;
import com.electionmonitor.model.User;
import com.electionmonitor.repository.CandidateRepository;
import com.electionmonitor.repository.ElectionRepository;
import com.electionmonitor.repository.UserRepository;
import com.electionmonitor.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElectionService {

    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<ElectionDTO> getAllElections() {
        return electionRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ElectionDTO getElectionById(Long id) {
        if (id == null) throw new IllegalArgumentException("Election ID cannot be null");
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + id));
        return toDTO(election);
    }

    @Transactional(readOnly = true)
    public List<ElectionDTO> getElectionsByStatus(String status) {
        Election.ElectionStatus electionStatus = Election.ElectionStatus.valueOf(status.toUpperCase());
        return electionRepository.findByStatus(electionStatus)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ElectionDTO createElection(ElectionDTO dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Election election = Election.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .electionType(Election.ElectionType.valueOf(dto.getElectionType().toUpperCase()))
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .status(Election.ElectionStatus.UPCOMING)
                .createdBy(user)
                .candidates(new java.util.ArrayList<>())
                .build();

        election = electionRepository.save(election);

        
        if (dto.getCandidates() != null && !dto.getCandidates().isEmpty()) {
            for (ElectionDTO.CandidateDTO candidateDTO : dto.getCandidates()) {
                Candidate candidate = Candidate.builder()
                        .name(candidateDTO.getName())
                        .party(candidateDTO.getParty())
                        .manifesto(candidateDTO.getManifesto())
                        .photoUrl(candidateDTO.getPhotoUrl())
                        .election(election)
                        .build();
                Candidate saved = candidateRepository.save(candidate);
                election.getCandidates().add(saved);
            }
        }

        return toDTO(election);
    }

    @Transactional
    public ElectionDTO updateElection(Long id, ElectionDTO dto) {
        if (id == null) throw new IllegalArgumentException("Election ID cannot be null");
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + id));

        election.setTitle(dto.getTitle());
        election.setDescription(dto.getDescription());
        election.setElectionType(Election.ElectionType.valueOf(dto.getElectionType().toUpperCase()));
        election.setStartDate(dto.getStartDate());
        election.setEndDate(dto.getEndDate());

        if (dto.getStatus() != null) {
            election.setStatus(Election.ElectionStatus.valueOf(dto.getStatus().toUpperCase()));
        }

        election = electionRepository.save(election);
        return toDTO(election);
    }

    public void deleteElection(Long id) {
        if (id == null) throw new IllegalArgumentException("Election ID cannot be null");
        if (!electionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Election not found with id: " + id);
        }
        electionRepository.deleteById(id);
    }

    @Transactional
    public ElectionDTO.CandidateDTO addCandidate(Long electionId, ElectionDTO.CandidateDTO dto) {
        if (electionId == null) throw new IllegalArgumentException("Election ID cannot be null");
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + electionId));

        Candidate candidate = Candidate.builder()
                .name(dto.getName())
                .party(dto.getParty())
                .manifesto(dto.getManifesto())
                .photoUrl(dto.getPhotoUrl())
                .election(election)
                .build();

        candidate = candidateRepository.save(candidate);
        
        
        if (election.getCandidates() != null) {
            election.getCandidates().add(candidate);
        }

        return modelMapper.map(candidate, ElectionDTO.CandidateDTO.class);
    }

    private ElectionDTO toDTO(Election election) {
        ElectionDTO dto = modelMapper.map(election, ElectionDTO.class);
        dto.setElectionType(election.getElectionType().name());
        dto.setStatus(election.getStatus().name());
        dto.setTotalVotes(voteRepository.countByElectionId(election.getId()));

        if (election.getCreatedBy() != null) {
            dto.setCreatedByName(election.getCreatedBy().getFullName());
        } else {
            dto.setCreatedByName("System / Deleted User");
        }

        if (dto.getCandidates() != null) {
            dto.getCandidates().forEach(cDto -> {
                if (cDto.getId() != null) {
                    cDto.setVoteCount(voteRepository.countByCandidateId(cDto.getId()));
                }
            });
        }

        return dto;
    }

}
