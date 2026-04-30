package com.electionmonitor.repository;

import com.electionmonitor.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByElectionIdAndUserId(Long electionId, Long userId);
    List<Vote> findByElectionId(Long electionId);
    List<Vote> findByUserId(Long userId);
    long countByElectionId(Long electionId);
    long countByCandidateId(Long candidateId);

    @Query("SELECT v.candidate.id, v.candidate.name, v.candidate.party, COUNT(v) FROM Vote v WHERE v.election.id = :electionId GROUP BY v.candidate.id, v.candidate.name, v.candidate.party")
    List<Object[]> getVoteCountsByElection(Long electionId);
}
