package org.example.school_bus_tracker_be.Model;



import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "emergencies")
public class Emergency {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Column(name = "admin_message")
    private String adminMessage;

    @Column(name = "parent_message")
    private String parentMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Emergency() {}

    public Emergency(Bus bus, User driver, double latitude, double longitude,
                     Severity severity, String adminMessage, String parentMessage, Status status) {
        this.bus = bus;
        this.driver = driver;
        this.latitude = latitude;
        this.longitude = longitude;
        this.severity = severity;
        this.adminMessage = adminMessage;
        this.parentMessage = parentMessage;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Bus getBus() { return bus; }
    public void setBus(Bus bus) { this.bus = bus; }

    public User getDriver() { return driver; }
    public void setDriver(User driver) { this.driver = driver; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public String getAdminMessage() { return adminMessage; }
    public void setAdminMessage(String adminMessage) { this.adminMessage = adminMessage; }

    public String getParentMessage() { return parentMessage; }
    public void setParentMessage(String parentMessage) { this.parentMessage = parentMessage; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public enum Severity { LOW, MEDIUM, HIGH }
    public enum Status { PENDING, RESOLVED }


}
