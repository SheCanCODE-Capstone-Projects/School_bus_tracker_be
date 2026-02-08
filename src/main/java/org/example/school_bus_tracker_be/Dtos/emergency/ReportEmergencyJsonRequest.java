package org.example.school_bus_tracker_be.Dtos.emergency;

import jakarta.validation.constraints.NotNull;
import org.example.school_bus_tracker_be.Enum.EmergencyType;

/**
 * JSON body for reporting an emergency (e.g. from mobile app).
 * Use this when sending Content-Type: application/json.
 * For voice recording, use the multipart/form-data endpoint instead.
 */
public class ReportEmergencyJsonRequest {

    @NotNull(message = "Emergency type is required")
    private EmergencyType type;

    private String description;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    public ReportEmergencyJsonRequest() {}

    public EmergencyType getType() {
        return type;
    }

    public void setType(EmergencyType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
