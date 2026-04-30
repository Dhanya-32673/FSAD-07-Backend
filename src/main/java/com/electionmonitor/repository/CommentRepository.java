package com.electionmonitor.repository;

import com.electionmonitor.model.DiscussionComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<DiscussionComment, Long> {
    List<DiscussionComment> findByDiscussionIdOrderByCreatedAtAsc(Long discussionId);
}
