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

    
    private final Map<String, long[]> otpStore = new ConcurrentHashMap<>();

    
    public void generateAndSend(String username, String adminEmail) {
        String otp = String.format("%06d", RANDOM.nextInt(1_000_000));
        long expiry = Instant.now().toEpochMilli() + OTP_VALIDITY_MS;
        otpStore.put(username, new long[]{Long.parseLong(otp), expiry});

        
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║  🔐 ADMIN OTP for [" + username + "]");
        System.out.println("║  CODE : " + otp);
        System.out.println("║  EMAIL: " + adminEmail);
        System.out.println("║  Valid for 5 minutes");
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
            System.err.println("⚠️ JavaMailSender not configured — email skipped. OTP is in logs above.");
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(senderEmail);
            msg.setTo(toEmail);   
            msg.setSubject("🔐 [" + otp + "] Bharat Vanguard Admin OTP");
            msg.setText(
                "BHARAT VANGUARD — ADMIN OTP\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                "Admin: " + username + "\n\n" +
                "Your verification code: " + otp + "\n\n" +
                "Valid for 5 minutes. Do not share this code.\n\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "Govt of India | Election Commission\n" +
                "Bharat Vanguard OS"
            );
            mailSender.send(msg);
            System.out.println("✅ OTP email sent to " + toEmail);
        } catch (Exception e) {
            System.err.println("⚠️ OTP email FAILED: " + e.getMessage());
            System.err.println("   Cause: " + e.getClass().getSimpleName());
            System.err.println("   Use the OTP from Railway logs above.");
        }
    }

    
    public boolean verify(String username, String inputOtp) {
        long[] stored = otpStore.get(username);
        if (stored == null) return false;

        long storedOtp = stored[0];
        long expiry    = stored[1];

        if (Instant.now().toEpochMilli() > expiry) {
            otpStore.remove(username);
            return false;
        }
        try {
            if (Long.parseLong(inputOtp) == storedOtp) {
                otpStore.remove(username);
                return true;
            }
        } catch (NumberFormatException ignored) {}
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
