package com.electionmonitor.repository;

import com.electionmonitor.model.CivicDiscussion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DiscussionRepository extends JpaRepository<CivicDiscussion, Long> {
    List<CivicDiscussion> findByAuthorId(Long authorId);
    List<CivicDiscussion> findByElectionId(Long electionId);
    List<CivicDiscussion> findAllByOrderByCreatedAtDesc();
}
