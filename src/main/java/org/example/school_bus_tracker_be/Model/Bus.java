package org.example.school_bus_tracker_be.Model;



import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "buses")
public class Bus {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @Column(name = "bus_number", nullable = false, unique = true)
    private String busNumber;

    @Column(name = "bus_name")
    private String busName;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "route")
    private String route;

    @Column(name = "plate_number", nullable = false, unique = true)
    private String plateNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_driver_id")
    private Driver assignedDriver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LocationPoint> locationPoints;

    // Constructors
    public Bus() {}

    public Bus(School school, User driver, String busNumber, String plateNumber, Status status) {
        this.school = school;
        this.driver = driver;
        this.busNumber = busNumber;
        this.plateNumber = plateNumber;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public School getSchool() { return school; }
    public void setSchool(School school) { this.school = school; }

    public User getDriver() { return driver; }
    public void setDriver(User driver) { this.driver = driver; }

    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }

    public String getBusName() { return busName; }
    public void setBusName(String busName) { this.busName = busName; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    public Driver getAssignedDriver() { return assignedDriver; }
    public void setAssignedDriver(Driver assignedDriver) { this.assignedDriver = assignedDriver; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public List<LocationPoint> getLocationPoints() { return locationPoints; }
    public void setLocationPoints(List<LocationPoint> locationPoints) { this.locationPoints = locationPoints; }

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public enum Status {
        ACTIVE,
        INACTIVE,
        MAINTENANCE
    }


}

