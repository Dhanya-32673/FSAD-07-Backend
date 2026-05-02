package com.electionmonitor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:dhanyaande@gmail.com}")
    private String senderEmail;

    private static final long OTP_VALIDITY_MS = 5 * 60 * 1000;
    private static final SecureRandom RANDOM = new SecureRandom();

    // Store OTP as String (to preserve leading zeros) and expiry as long
    private final Map<String, String[]> otpStore = new ConcurrentHashMap<>();

    public void generateAndSend(String username, String adminEmail) {
        String otp = String.format("%06d", RANDOM.nextInt(1_000_000));
        long expiry = Instant.now().toEpochMilli() + OTP_VALIDITY_MS;
        otpStore.put(username, new String[]{otp, String.valueOf(expiry)});

        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║  ADMIN OTP for [" + username + "]");
        System.out.println("║  CODE  : " + otp);
        System.out.println("║  TO    : " + adminEmail);
        System.out.println("║  FROM  : " + senderEmail);
        System.out.println("║  MAILER: " + (mailSender != null ? "CONFIGURED" : "NULL - email will NOT send"));
        System.out.println("╚══════════════════════════════════════════╝\n");

        final String otpCopy = otp;
        Thread emailThread = new Thread(() -> sendEmail(username, adminEmail, otpCopy));
        emailThread.setDaemon(true);
        emailThread.start();
    }

    public void generateAndSend(String username) {
        generateAndSend(username, senderEmail);
    }

    private void sendEmail(String username, String toEmail, String otp) {
        if (mailSender == null) {
            System.err.println("MAIL_ERROR: JavaMailSender is NULL — spring.mail.* properties not loaded.");
            System.err.println("MAIL_ERROR: Check GMAIL_EMAIL and GMAIL_APP_PASSWORD env vars on Railway.");
            System.err.println("MAIL_FALLBACK: Use OTP from logs above.");
            return;
        }
        try {
            System.out.println("MAIL_ATTEMPT: Connecting to smtp.gmail.com:587 ...");
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
            System.out.println("MAIL_SUCCESS: OTP sent to " + toEmail);
        } catch (Exception e) {
            System.err.println("MAIL_ERROR: " + e.getClass().getName() + " - " + e.getMessage());
            Throwable cause = e.getCause();
            int depth = 0;
            while (cause != null && depth < 5) {
                System.err.println("MAIL_CAUSE[" + depth + "]: " + cause.getClass().getName() + " - " + cause.getMessage());
                cause = cause.getCause();
                depth++;
            }
            System.err.println("MAIL_FALLBACK: Use OTP from logs above.");
        }
    }

    public boolean verify(String username, String inputOtp) {
        String[] stored = otpStore.get(username);
        if (stored == null) {
            System.err.println("OTP_VERIFY: No OTP found in store for user [" + username + "]");
            return false;
        }

        String storedOtp = stored[0];
        long expiry = Long.parseLong(stored[1]);

        if (Instant.now().toEpochMilli() > expiry) {
            otpStore.remove(username);
            System.err.println("OTP_VERIFY: OTP expired for user [" + username + "]");
            return false;
        }

        if (inputOtp != null && inputOtp.trim().equals(storedOtp)) {
            otpStore.remove(username);
            System.out.println("OTP_VERIFY: OTP verified successfully for [" + username + "]");
            return true;
        }

        System.err.println("OTP_VERIFY: Mismatch for [" + username + "] input=[" + inputOtp + "] expected=[" + storedOtp + "]");
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
