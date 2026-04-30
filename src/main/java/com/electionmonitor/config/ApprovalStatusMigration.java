package com.electionmonitor.config;

import com.electionmonitor.model.User;
import com.electionmonitor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ApprovalStatusMigration {

    private static final String ADMIN_EMAIL = "2400032673cse1@gmail.com";

    private final UserRepository userRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void migrateApprovalStatus() {
        fixApprovalStatuses();
        fixAdminEmail();
    }

    
    private void fixApprovalStatuses() {
        try {
            List<User> allUsers = userRepository.findAll();
            long approvedCount = allUsers.stream()
                    .filter(u -> u.getApprovalStatus() == User.ApprovalStatus.APPROVED)
                    .count();

            boolean bootstrapNeeded = (approvedCount == 0 && !allUsers.isEmpty());

            int fixed = 0;
            for (User user : allUsers) {
                boolean isNull = (user.getApprovalStatus() == null);
                boolean isBadPending = (bootstrapNeeded && user.getApprovalStatus() == User.ApprovalStatus.PENDING);
                if (isNull || isBadPending) {
                    user.setApprovalStatus(User.ApprovalStatus.APPROVED);
                    userRepository.save(user);
                    fixed++;
                }
            }

            if (fixed > 0) {
                System.out.println("✅ ApprovalStatusMigration: Fixed " + fixed + " users → APPROVED"
                        + (bootstrapNeeded ? " (bootstrap)" : " (null values)"));
            } else {
                System.out.println("✅ ApprovalStatusMigration: No approval fixes needed.");
            }
        } catch (Exception e) {
            System.err.println("⚠️ ApprovalStatusMigration failed (non-critical): " + e.getMessage());
        }
    }

    
    private void fixAdminEmail() {
        try {
            userRepository.findByUsername("admin").ifPresent(admin -> {
                if (!ADMIN_EMAIL.equals(admin.getEmail())) {
                    String oldEmail = admin.getEmail();
                    admin.setEmail(ADMIN_EMAIL);
                    userRepository.save(admin);
                    System.out.println("✅ Admin email updated: [" + oldEmail + "] → [" + ADMIN_EMAIL + "]");
                } else {
                    System.out.println("✅ Admin email already correct: " + ADMIN_EMAIL);
                }
            });
        } catch (Exception e) {
            System.err.println("⚠️ Admin email migration failed (non-critical): " + e.getMessage());
        }
    }
}
