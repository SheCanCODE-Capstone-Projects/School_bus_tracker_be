package org.example.school_bus_tracker_be.serviceimpl;

import org.example.school_bus_tracker_be.Service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendPasswordResetCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Password Reset Verification Code - School Bus Tracker");
        message.setText(buildPasswordResetEmailContent(code));

        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email: " + e.getMessage());
        }
    }

    @Override
    public void sendNotificationEmail(String to, String subject, String messageBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(messageBody);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send notification email: " + e.getMessage());
        }
    }

    private String buildPasswordResetEmailContent(String code) {
        return String.format(
            "Hello,\n\n" +
            "You have requested to reset your password for School Bus Tracker.\n\n" +
            "Your verification code is: %s\n\n" +
            "Please enter this code in the password reset form to proceed.\n\n" +
            "This code will expire in 10 minutes.\n\n" +
            "If you did not request this password reset, please ignore this email.\n\n" +
            "Best regards,\n" +
            "School Bus Tracker Team",
            code
        );
    }
}
