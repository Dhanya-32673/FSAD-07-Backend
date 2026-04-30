package com.electionmonitor.controller;

import com.electionmonitor.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mail-test")
public class MailTestController {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.host:NOT_SET}")
    private String mailHost;

    @Value("${spring.mail.port:0}")
    private int mailPort;

    @Value("${spring.mail.username:NOT_SET}")
    private String mailUsername;

    @Value("${spring.mail.password:NOT_SET}")
    private String mailPassword;

    @GetMapping("/status")
    public Map<String, Object> status() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("mailSenderBean", mailSender != null ? "CONFIGURED" : "NULL - autoconfiguration failed");
        info.put("host", mailHost);
        info.put("port", mailPort);
        info.put("username", mailUsername);
        info.put("passwordLength", mailPassword != null ? mailPassword.length() : 0);
        info.put("passwordSet", mailPassword != null && !mailPassword.isEmpty() && !mailPassword.equals("NOT_SET"));
        return info;
    }

    @GetMapping("/send")
    public Map<String, Object> send(@RequestParam(defaultValue = "2400032673cse1@gmail.com") String to) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("to", to);
        result.put("from", mailUsername);

        if (mailSender == null) {
            result.put("status", "FAILED");
            result.put("error", "JavaMailSender bean is NULL — SMTP autoconfiguration failed. Check spring.mail.* properties.");
            return result;
        }

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(mailUsername);
            msg.setTo(to);
            msg.setSubject("Railway SMTP Test - Election Monitor");
            msg.setText("This is a test email from Railway deployment.\n\nIf you received this, SMTP is working correctly.");
            mailSender.send(msg);
            result.put("status", "SUCCESS");
            result.put("message", "Email sent successfully to " + to);
        } catch (Exception e) {
            result.put("status", "FAILED");
            result.put("error", e.getMessage());
            result.put("errorType", e.getClass().getName());

            StringBuilder causes = new StringBuilder();
            Throwable cause = e.getCause();
            int depth = 0;
            while (cause != null && depth < 4) {
                causes.append("[").append(depth).append("] ")
                      .append(cause.getClass().getSimpleName())
                      .append(": ").append(cause.getMessage()).append(" | ");
                cause = cause.getCause();
                depth++;
            }
            result.put("causesChain", causes.toString());
        }
        return result;
    }
}
