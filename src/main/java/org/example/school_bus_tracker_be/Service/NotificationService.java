package org.example.school_bus_tracker_be.Service;

import org.example.school_bus_tracker_be.DTO.NotificationResponse;
import org.example.school_bus_tracker_be.Model.Notification;
import org.example.school_bus_tracker_be.Model.User;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getUserNotifications(Long userId);
    NotificationResponse markAsRead(Long userId, Long notificationId);
    Notification createNotification(User user, String title, String message, Notification.Type type);
}
