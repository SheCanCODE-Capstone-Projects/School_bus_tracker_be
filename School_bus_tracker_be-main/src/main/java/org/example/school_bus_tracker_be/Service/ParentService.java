package org.example.school_bus_tracker_be.Service;

import org.example.school_bus_tracker_be.DTO.*;
import org.example.school_bus_tracker_be.Dtos.auth.AuthResponse;
import java.util.List;

public interface ParentService {
    AuthResponse registerParentWithStudents(ParentRegisterWithStudentsRequest request);
    List<StudentResponse> getParentStudents(Long parentId);
    BusLocationResponse getBusLocation(Long busId);
    List<NotificationResponse> getParentNotifications(Long parentId);
}
