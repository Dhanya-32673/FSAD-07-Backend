package com.electionmonitor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpService.class);

    private final EmailService emailService;

    private static final long OTP_VALIDITY_MS = 5 * 60 * 1000;
    private static final SecureRandom RANDOM = new SecureRandom();

    // Store OTP as String (to preserve leading zeros) and expiry as long
    private final Map<String, String[]> otpStore = new ConcurrentHashMap<>();

    public OtpService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void generateAndSend(String username, String adminEmail) {
        String otp = String.format("%06d", RANDOM.nextInt(1_000_000));
        long expiry = Instant.now().toEpochMilli() + OTP_VALIDITY_MS;
        otpStore.put(username, new String[]{otp, String.valueOf(expiry)});

        log.info("\n╔══════════════════════════════════════════╗");
        log.info("║  ADMIN OTP for [{}]", username);
        log.info("║  CODE  : {}", otp);
        log.info("║  TO    : {}", adminEmail);
        log.info("║  MAILER: {}", emailService.isConfigured() ? "CONFIGURED" : "NULL - email will NOT send");
        log.info("╚══════════════════════════════════════════╝\n");

        // Delegate to EmailService — a separate Spring bean where @Async works
        // (self-invocation within the same class bypasses Spring's AOP proxy)
        emailService.sendOtpEmail(username, adminEmail, otp);
    }

    public void generateAndSend(String username) {
        generateAndSend(username, "dhanyaande@gmail.com");
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
