package org.example.school_bus_tracker_be.Service;

import org.example.school_bus_tracker_be.Dtos.emergency.*;
import org.example.school_bus_tracker_be.Model.Emergency;
import java.util.List;

public interface EmergencyService {
    ReportEmergencyResponse reportEmergency(ReportEmergencyRequest request, Long driverId);
    EmergencyStatsResponse getEmergencyStats();
    List<EmergencyResponse> getEmergencies(Emergency.Status status);
    EmergencyResponse getEmergencyById(Long emergencyId, Long userId, String userRole);
    void resolveEmergency(Long emergencyId, ResolveEmergencyRequest request, Long adminId);
    List<EmergencyResponse> getDriverEmergencies(Long driverId);
    EmergencyResponse getDriverEmergencyById(Long emergencyId, Long driverId);
    List<EmergencyResponse> getParentEmergencies(Long parentId);
    EmergencyResponse getParentEmergencyById(Long emergencyId, Long parentId);
}
