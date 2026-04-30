package com.electionmonitor.controller;

import com.electionmonitor.model.PollingStation;
import com.electionmonitor.exception.ResourceNotFoundException;
import com.electionmonitor.repository.PollingStationRepository;
import com.electionmonitor.repository.ElectionRepository;
import com.electionmonitor.model.Election;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/polling-stations")
@RequiredArgsConstructor
public class PollingStationController {

    private final PollingStationRepository pollingStationRepository;
    private final ElectionRepository electionRepository;

    @GetMapping
    public ResponseEntity<List<PollingStation>> getAllStations() {
        return ResponseEntity.ok(pollingStationRepository.findAll());
    }

    @GetMapping("/election/{electionId}")
    public ResponseEntity<List<PollingStation>> getStationsByElection(@PathVariable Long electionId) {
        return ResponseEntity.ok(pollingStationRepository.findByElectionId(electionId));
    }

    @PostMapping
    public ResponseEntity<PollingStation> createStation(@RequestBody Map<String, Object> body) {
        Long electionId = Long.valueOf(body.get("electionId").toString());
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found"));

        PollingStation station = PollingStation.builder()
                .name((String) body.get("name"))
                .location((String) body.get("location"))
                .region((String) body.get("region"))
                .totalVoters(body.get("totalVoters") != null ? Integer.parseInt(body.get("totalVoters").toString()) : 0)
                .votesCast(0)
                .status(PollingStation.StationStatus.NOT_STARTED)
                .election(election)
                .build();

        return ResponseEntity.ok(pollingStationRepository.save(station));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PollingStation> updateStation(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        PollingStation station = pollingStationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Polling station not found"));

        if (body.containsKey("votesCast")) {
            station.setVotesCast(Integer.parseInt(body.get("votesCast").toString()));
        }
        if (body.containsKey("status")) {
            station.setStatus(PollingStation.StationStatus.valueOf(body.get("status").toString().toUpperCase()));
        }

        return ResponseEntity.ok(pollingStationRepository.save(station));
    }
}
