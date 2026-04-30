package com.electionmonitor.repository;

import com.electionmonitor.model.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {
    List<Election> findByStatus(Election.ElectionStatus status);
    List<Election> findByElectionType(Election.ElectionType electionType);
    long countByStatus(Election.ElectionStatus status);
    List<Election> findAllByOrderByCreatedAtDesc();
}
