package org.example.school_bus_tracker_be.Service;

import org.example.school_bus_tracker_be.Dtos.location.*;
import java.time.LocalDateTime;
import java.util.List;

public interface LocationService {
    // Driver methods
    void startTracking(Long driverId);
    void updateLocation(Long driverId, LocationUpdateRequest request);
    void stopTracking(Long driverId);
    
    // Parent methods
    LocationResponse getBusLocation(Long busId, Long parentId);
    
    // Admin methods
    TrackingStatusResponse getTrackingStatus(Long busId);
    List<LocationResponse> getLocationHistory(Long busId, LocalDateTime from, LocalDateTime to);
}
