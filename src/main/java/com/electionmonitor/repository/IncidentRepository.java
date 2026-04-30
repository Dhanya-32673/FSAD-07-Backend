package com.electionmonitor.repository;

import com.electionmonitor.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByElectionId(Long electionId);
    List<Incident> findByReportedById(Long userId);
    List<Incident> findByStatus(Incident.IncidentStatus status);
    List<Incident> findByCategory(Incident.IncidentCategory category);
    long countByStatus(Incident.IncidentStatus status);
    long countByCategory(Incident.IncidentCategory category);
    long countBySeverity(Incident.Severity severity);
    long countByElectionId(Long electionId);
    List<Incident> findAllByOrderByReportedAtDesc();
}
