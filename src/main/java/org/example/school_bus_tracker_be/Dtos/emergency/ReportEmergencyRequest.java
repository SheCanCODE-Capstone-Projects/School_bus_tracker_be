package org.example.school_bus_tracker_be.Dtos.emergency;

import org.example.school_bus_tracker_be.Enum.EmergencyType;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public class ReportEmergencyRequest {
    @NotNull(message = "Emergency type is required")
    private EmergencyType type;
    
    private String description;
    
    private MultipartFile voiceAudio;
    
    @NotNull(message = "Latitude is required")
    private Double latitude;
    
    @NotNull(message = "Longitude is required")
    private Double longitude;

    public ReportEmergencyRequest() {}

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

    public MultipartFile getVoiceAudio() {
        return voiceAudio;
    }

    public void setVoiceAudio(MultipartFile voiceAudio) {
        this.voiceAudio = voiceAudio;
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
