package org.example.school_bus_tracker_be.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(name = "bus_name", nullable = false)
    private String busName;

    @Column(name = "bus_number", nullable = false, unique = true)
    private String busNumber;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "route")
    private String route;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_driver_id")
    private Driver assignedDriver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<LocationPoint> locationPoints;

    @OneToMany(mappedBy = "assignedBus", cascade = CascadeType.ALL)
    private List<Student> students;

    // Constructors
    public Bus() {}

    public Bus(School school, String busName, String busNumber, Integer capacity, String route, Status status) {
        this.school = school;
        this.busName = busName;
        this.busNumber = busNumber;
        this.capacity = capacity;
        this.route = route;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public School getSchool() { return school; }
    public void setSchool(School school) { this.school = school; }

    public String getBusName() { return busName; }
    public void setBusName(String busName) { this.busName = busName; }

    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }

    public Driver getAssignedDriver() { return assignedDriver; }
    public void setAssignedDriver(Driver assignedDriver) { this.assignedDriver = assignedDriver; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public List<LocationPoint> getLocationPoints() { return locationPoints; }
    public void setLocationPoints(List<LocationPoint> locationPoints) { this.locationPoints = locationPoints; }

    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public enum Status {
        ACTIVE,
        INACTIVE,
        MAINTENANCE
    }


}

