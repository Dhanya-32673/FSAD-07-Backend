package com.electionmonitor.service;

import com.electionmonitor.dto.IncidentDTO;
import com.electionmonitor.exception.ResourceNotFoundException;
import com.electionmonitor.model.Election;
import com.electionmonitor.model.Incident;
import com.electionmonitor.model.User;
import com.electionmonitor.repository.ElectionRepository;
import com.electionmonitor.repository.IncidentRepository;
import com.electionmonitor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final ElectionRepository electionRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<IncidentDTO> getAllIncidents() {
        return incidentRepository.findAllByOrderByReportedAtDesc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public IncidentDTO getIncidentById(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with id: " + id));
        return toDTO(incident);
    }

    @Transactional(readOnly = true)
    public List<IncidentDTO> getIncidentsByElection(Long electionId) {
        return incidentRepository.findByElectionId(electionId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IncidentDTO> getIncidentsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return incidentRepository.findByReportedById(user.getId())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public IncidentDTO createIncident(IncidentDTO dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Election election = electionRepository.findById(dto.getElectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Election not found"));

        Incident incident = Incident.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(Incident.IncidentCategory.valueOf(dto.getCategory().toUpperCase()))
                .severity(Incident.Severity.valueOf(dto.getSeverity().toUpperCase()))
                .location(dto.getLocation())
                .reportedBy(user)
                .election(election)
                .build();

        incident = incidentRepository.save(incident);
        return toDTO(incident);
    }

    public IncidentDTO updateIncidentStatus(Long id, String status) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with id: " + id));

        incident.setStatus(Incident.IncidentStatus.valueOf(status.toUpperCase()));

        if (status.equalsIgnoreCase("RESOLVED")) {
            incident.setResolvedAt(LocalDateTime.now());
        }

        incidentRepository.save(incident);
        return toDTO(incident);
    }

    private IncidentDTO toDTO(Incident incident) {
        IncidentDTO dto = modelMapper.map(incident, IncidentDTO.class);
        dto.setCategory(incident.getCategory().name());
        dto.setSeverity(incident.getSeverity().name());
        dto.setStatus(incident.getStatus().name());
        dto.setElectionId(incident.getElection().getId());
        dto.setElectionTitle(incident.getElection().getTitle());
        dto.setReportedByName(incident.getReportedBy().getFullName());
        dto.setReportedById(incident.getReportedBy().getId());
        return dto;
    }
}
