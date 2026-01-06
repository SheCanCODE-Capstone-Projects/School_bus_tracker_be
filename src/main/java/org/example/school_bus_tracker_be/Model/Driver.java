package org.example.school_bus_tracker_be.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "license_number", nullable = false, unique = true)
    private String licenseNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_bus_id")
    private Bus assignedBus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Driver() {}

    public Driver(School school, String fullName, String email, String phoneNumber, String licenseNumber) {
        this.school = school;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.licenseNumber = licenseNumber;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public School getSchool() { return school; }
    public void setSchool(School school) { this.school = school; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public Bus getAssignedBus() { return assignedBus; }
    public void setAssignedBus(Bus assignedBus) { this.assignedBus = assignedBus; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }
}