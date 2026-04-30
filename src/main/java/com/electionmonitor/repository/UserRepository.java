package com.electionmonitor.repository;

import com.electionmonitor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByRole(User.Role role);
    long countByRole(User.Role role);
    long countByEnabled(boolean enabled);
    List<User> findByApprovalStatus(User.ApprovalStatus approvalStatus);
}
