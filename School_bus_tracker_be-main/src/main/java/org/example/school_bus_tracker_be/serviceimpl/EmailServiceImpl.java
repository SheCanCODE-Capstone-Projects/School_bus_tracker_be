package org.example.school_bus_tracker_be.serviceimpl;

import org.example.school_bus_tracker_be.Service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "spring.mail.host", matchIfMissing = false)
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Request - School Bus Tracker");
        message.setText(buildPasswordResetEmailContent(token));
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    private String buildPasswordResetEmailContent(String token) {
        return String.format(
            "Hello,\n\n" +
            "You have requested to reset your password for School Bus Tracker.\n\n" +
            "Please click the following link to reset your password:\n" +
            "%s/reset-password?token=%s\n\n" +
            "This link will expire in 1 hour.\n\n" +
            "If you did not request this password reset, please ignore this email.\n\n" +
            "Best regards,\n" +
            "School Bus Tracker Team",
            frontendUrl, token
        );
    }
}