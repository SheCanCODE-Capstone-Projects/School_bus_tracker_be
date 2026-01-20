package org.example.school_bus_tracker_be.Model;

import org.example.school_bus_tracker_be.Enum.EmergencyType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "emergencies")
public class Emergency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmergencyType type;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "voice_recording_url")
    private String voiceRecordingUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(name = "parents_notified", nullable = false)
    private Boolean parentsNotified = false;

    @Column(name = "reported_at", nullable = false, updatable = false)
    private LocalDateTime reportedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_admin_id")
    private User resolvedByAdmin;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    public Emergency() {}

    public Emergency(EmergencyType type, String description, Bus bus, User driver, 
                     double latitude, double longitude) {
        this.type = type;
        this.description = description;
        this.bus = bus;
        this.driver = driver;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = Status.ACTIVE;
        this.parentsNotified = false;
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

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Bus getBus() { return bus; }
    public void setBus(Bus bus) { this.bus = bus; }

    public User getDriver() { return driver; }
    public void setDriver(User driver) { this.driver = driver; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public Boolean getParentsNotified() { return parentsNotified; }
    public void setParentsNotified(Boolean parentsNotified) { this.parentsNotified = parentsNotified; }

    public LocalDateTime getReportedAt() { return reportedAt; }
    public void setReportedAt(LocalDateTime reportedAt) { this.reportedAt = reportedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public User getResolvedByAdmin() { return resolvedByAdmin; }
    public void setResolvedByAdmin(User resolvedByAdmin) { this.resolvedByAdmin = resolvedByAdmin; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

    @PrePersist
    protected void onCreate() { 
        this.reportedAt = LocalDateTime.now();
    }

    public enum Status { 
        ACTIVE, 
        RESOLVED 
    }
}
