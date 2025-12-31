package org.example.school_bus_tracker_be.serviceimpl;

import org.example.school_bus_tracker_be.DTO.*;
import org.example.school_bus_tracker_be.Dtos.auth.AuthResponse;
import org.example.school_bus_tracker_be.Service.ParentService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ParentServiceImpl implements ParentService {

    @Override
    public AuthResponse registerParentWithStudents(ParentRegisterWithStudentsRequest request) {
        // TODO: Implement parent registration with students
        return new AuthResponse("mock-jwt-token", 3600000L, "PARENT");
    }

    @Override
    public List<StudentResponse> getParentStudents(Long parentId) {
        List<StudentResponse> students = new ArrayList<>();
        
        if (parentId == 1) {
            students.add(new StudentResponse(1L, "Aurore", "ST101", 12, "P6", "FEMALE"));
        } else if (parentId == 2) {
            students.add(new StudentResponse(2L, "Emma Smith", "ST102", 10, "P4", "FEMALE"));
            students.add(new StudentResponse(3L, "James Smith", "ST103", 8, "P2", "MALE"));
        } else if (parentId == 3) {
            students.add(new StudentResponse(4L, "Carlos Garcia", "ST104", 14, "S1", "MALE"));
        } else {
            // Return empty list for unknown parent IDs
        }
        
        return students;
    }

    @Override
    public BusLocationResponse getBusLocation(Long busId) {
        if (busId == 1) {
            return new BusLocationResponse(-1.9441, 30.0632, 35, "MOVING");
        } else if (busId == 2) {
            return new BusLocationResponse(-1.9500, 30.0700, 45, "MOVING");
        } else if (busId == 3) {
            return new BusLocationResponse(-1.9300, 30.0500, 0, "STOPPED");
        } else {
            return new BusLocationResponse(0.0, 0.0, 0, "NOT_FOUND");
        }
    }

    @Override
    public List<NotificationResponse> getParentNotifications(Long parentId) {
        List<NotificationResponse> notifications = new ArrayList<>();
        
        if (parentId == 1) {
            notifications.add(new NotificationResponse(1L, "Bus Update", "Your child's bus is on the way", "INFO", false));
            notifications.add(new NotificationResponse(2L, "Arrival Alert", "Bus arriving in 5 minutes", "WARNING", false));
        } else if (parentId == 2) {
            notifications.add(new NotificationResponse(3L, "Departure Notice", "Bus has left school", "INFO", true));
            notifications.add(new NotificationResponse(4L, "Emergency Alert", "Traffic delay expected", "EMERGENCY", false));
        } else if (parentId == 3) {
            notifications.add(new NotificationResponse(5L, "Schedule Change", "Bus route updated", "WARNING", false));
        }
        
        return notifications;
    }
}
