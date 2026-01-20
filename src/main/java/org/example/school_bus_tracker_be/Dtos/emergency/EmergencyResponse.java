package org.example.school_bus_tracker_be.Dtos.emergency;

import org.example.school_bus_tracker_be.Enum.EmergencyType;
import org.example.school_bus_tracker_be.Model.Emergency;
import java.time.LocalDateTime;

public class EmergencyResponse {
    private Long id;
    private EmergencyType type;
    private String description;
    private String voiceRecordingUrl;
    private Emergency.Status status;
    private String busNumber;
    private String driverName;
    private Double latitude;
    private Double longitude;
    private Boolean parentsNotified;
    private LocalDateTime reportedAt;
    private LocalDateTime resolvedAt;
    private String resolvedByAdminName;
    private String resolutionNotes;

    public EmergencyResponse() {}

    public EmergencyResponse(Emergency emergency) {
        this.id = emergency.getId();
        this.type = emergency.getType();
        this.description = emergency.getDescription();
        this.voiceRecordingUrl = emergency.getVoiceRecordingUrl();
        this.status = emergency.getStatus();
        this.busNumber = emergency.getBus() != null ? emergency.getBus().getBusNumber() : null;
        this.driverName = emergency.getDriver() != null ? emergency.getDriver().getName() : null;
        this.latitude = emergency.getLatitude();
        this.longitude = emergency.getLongitude();
        this.parentsNotified = emergency.getParentsNotified();
        this.reportedAt = emergency.getReportedAt();
        this.resolvedAt = emergency.getResolvedAt();
        this.resolvedByAdminName = emergency.getResolvedByAdmin() != null ? emergency.getResolvedByAdmin().getName() : null;
        this.resolutionNotes = emergency.getResolutionNotes();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EmergencyType getType() { return type; }
    public void setType(EmergencyType type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getVoiceRecordingUrl() { return voiceRecordingUrl; }
    public void setVoiceRecordingUrl(String voiceRecordingUrl) { this.voiceRecordingUrl = voiceRecordingUrl; }

    public Emergency.Status getStatus() { return status; }
    public void setStatus(Emergency.Status status) { this.status = status; }

    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Boolean getParentsNotified() { return parentsNotified; }
    public void setParentsNotified(Boolean parentsNotified) { this.parentsNotified = parentsNotified; }

    public LocalDateTime getReportedAt() { return reportedAt; }
    public void setReportedAt(LocalDateTime reportedAt) { this.reportedAt = reportedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public String getResolvedByAdminName() { return resolvedByAdminName; }
    public void setResolvedByAdminName(String resolvedByAdminName) { this.resolvedByAdminName = resolvedByAdminName; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
}
