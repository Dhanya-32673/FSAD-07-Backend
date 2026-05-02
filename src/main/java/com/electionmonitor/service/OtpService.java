package com.electionmonitor.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:dhanyaande@gmail.com}")
    private String senderEmail;

    @Value("${spring.mail.password:NOT_SET}")
    private String mailPasswordForDiag;

    private static final long OTP_VALIDITY_MS = 5 * 60 * 1000;
    private static final SecureRandom RANDOM = new SecureRandom();

    // Store OTP as String (to preserve leading zeros) and expiry as long
    private final Map<String, String[]> otpStore = new ConcurrentHashMap<>();

    /**
     * Startup diagnostic — logs mail configuration status so deployment issues
     * are visible immediately in Railway/Render logs.
     */
    @PostConstruct
    public void logMailConfig() {
        log.info("╔══════════════════════════════════════════════════════════╗");
        log.info("║  MAIL CONFIGURATION DIAGNOSTIC                         ║");
        log.info("║  JavaMailSender : {}", mailSender != null ? "CONFIGURED ✅" : "NULL ❌ — emails will NOT send");
        log.info("║  Sender (from)  : {}", senderEmail);
        log.info("║  Password length: {}", mailPasswordForDiag != null ? mailPasswordForDiag.length() : 0);
        log.info("║  Password set   : {}", mailPasswordForDiag != null && !mailPasswordForDiag.isEmpty() && !"NOT_SET".equals(mailPasswordForDiag));
        log.info("╚══════════════════════════════════════════════════════════╝");

        if (mailSender == null) {
            log.error("MAIL_CRITICAL: JavaMailSender bean is NULL. Spring could not autoconfigure mail.");
            log.error("MAIL_CRITICAL: Ensure GMAIL_EMAIL and GMAIL_APP_PASSWORD env vars are set on Railway.");
            log.error("MAIL_CRITICAL: Or set SPRING_MAIL_USERNAME and SPRING_MAIL_PASSWORD directly.");
        }
    }

    public void generateAndSend(String username, String adminEmail) {
        String otp = String.format("%06d", RANDOM.nextInt(1_000_000));
        long expiry = Instant.now().toEpochMilli() + OTP_VALIDITY_MS;
        otpStore.put(username, new String[]{otp, String.valueOf(expiry)});

        log.info("\n╔══════════════════════════════════════════╗");
        log.info("║  ADMIN OTP for [{}]", username);
        log.info("║  CODE  : {}", otp);
        log.info("║  TO    : {}", adminEmail);
        log.info("║  FROM  : {}", senderEmail);
        log.info("║  MAILER: {}", mailSender != null ? "CONFIGURED" : "NULL - email will NOT send");
        log.info("╚══════════════════════════════════════════╝\n");

        // Send email asynchronously via Spring's @Async (managed thread pool)
        // instead of a raw daemon thread that can be killed on deployed platforms
        sendEmailAsync(username, adminEmail, otp);
    }

    public void generateAndSend(String username) {
        generateAndSend(username, senderEmail);
    }

    /**
     * Sends the OTP email asynchronously. Uses @Async so Spring manages the thread
     * via a proper task executor, preventing the daemon-thread issue on Railway/Render
     * where raw daemon threads get terminated before SMTP completes.
     */
    @Async
    public void sendEmailAsync(String username, String toEmail, String otp) {
        if (mailSender == null) {
            log.error("MAIL_ERROR: JavaMailSender is NULL — spring.mail.* properties not loaded.");
            log.error("MAIL_ERROR: Check GMAIL_EMAIL and GMAIL_APP_PASSWORD env vars on Railway.");
            log.error("MAIL_FALLBACK: Use OTP from logs above.");
            return;
        }
        try {
            log.info("MAIL_ATTEMPT: Connecting to smtp.gmail.com:587 for user [{}]...", username);
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(senderEmail);
            msg.setTo(toEmail);
            msg.setSubject("[" + otp + "] Bharat Vanguard Admin OTP");
            msg.setText(
                "BHARAT VANGUARD - ADMIN OTP\n" +
                "--------------------------\n\n" +
                "Admin: " + username + "\n\n" +
                "Your verification code: " + otp + "\n\n" +
                "Valid for 5 minutes. Do not share.\n\n" +
                "--------------------------\n" +
                "Election Commission of India"
            );
            mailSender.send(msg);
            log.info("MAIL_SUCCESS: OTP email sent to {} for user [{}]", toEmail, username);
        } catch (Exception e) {
            log.error("MAIL_ERROR: {} - {}", e.getClass().getName(), e.getMessage());
            Throwable cause = e.getCause();
            int depth = 0;
            while (cause != null && depth < 5) {
                log.error("MAIL_CAUSE[{}]: {} - {}", depth, cause.getClass().getName(), cause.getMessage());
                cause = cause.getCause();
                depth++;
            }
            log.error("MAIL_FALLBACK: Use OTP from logs above.");
        }
    }

    public boolean verify(String username, String inputOtp) {
        String[] stored = otpStore.get(username);
        if (stored == null) {
            log.warn("OTP_VERIFY: No OTP found in store for user [{}]", username);
            return false;
        }

        String storedOtp = stored[0];
        long expiry = Long.parseLong(stored[1]);

        if (Instant.now().toEpochMilli() > expiry) {
            otpStore.remove(username);
            log.warn("OTP_VERIFY: OTP expired for user [{}]", username);
            return false;
        }

        if (inputOtp != null && inputOtp.trim().equals(storedOtp)) {
            otpStore.remove(username);
            log.info("OTP_VERIFY: OTP verified successfully for [{}]", username);
            return true;
        }

        log.warn("OTP_VERIFY: Mismatch for [{}] input=[{}] expected=[{}]", username, inputOtp, storedOtp);
        return false;
    }

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "****@****.com";
        String[] parts = email.split("@");
        String local = parts[0];
        String domain = parts[1];
        if (local.length() <= 2) return "*".repeat(local.length()) + "@" + domain;
        return local.charAt(0) + "*".repeat(local.length() - 2) + local.charAt(local.length() - 1) + "@" + domain;
    }
}
