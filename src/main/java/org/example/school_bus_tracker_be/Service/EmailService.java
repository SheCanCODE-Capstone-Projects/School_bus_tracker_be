package org.example.school_bus_tracker_be.Service;

public interface EmailService {
    void sendPasswordResetCode(String to, String code);
    void sendNotificationEmail(String to, String subject, String message);
}
