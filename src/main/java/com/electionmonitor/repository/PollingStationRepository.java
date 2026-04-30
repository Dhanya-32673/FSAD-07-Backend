package com.electionmonitor.repository;

import com.electionmonitor.model.PollingStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PollingStationRepository extends JpaRepository<PollingStation, Long> {
    List<PollingStation> findByElectionId(Long electionId);
    List<PollingStation> findByStatus(PollingStation.StationStatus status);
    long countByElectionId(Long electionId);
}
