package com.electionmonitor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * Dedicated email service — exists as a separate bean so that Spring's
 * proxy-based @Async actually works.  (Self-invocation within the same
 * class bypasses the proxy and runs synchronously.)
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:dhanyaande@gmail.com}")
    private String senderEmail;

    @Value("${spring.mail.password:NOT_SET}")
    private String mailPasswordForDiag;

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
        }
    }

    public boolean isConfigured() {
        return mailSender != null;
    }

    /**
     * Sends an OTP email asynchronously.  Because this is a DIFFERENT bean
     * from OtpService, Spring's @Async proxy intercept works correctly —
     * the caller returns immediately while this runs in a separate thread.
     */
    @Async
    public void sendOtpEmail(String username, String toEmail, String otp) {
        if (mailSender == null) {
            log.error("MAIL_ERROR: JavaMailSender is NULL — spring.mail.* properties not loaded.");
            log.error("MAIL_FALLBACK: Use OTP from console logs.");
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
            log.error("MAIL_FALLBACK: Use OTP from console logs.");
        }
    }
}
