package org.example.school_bus_tracker_be.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdminAddDriverRequest {

    @NotBlank(message = "Full name is required")
    @JsonProperty("full_name")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("license_number")
    private String licenseNumber;

    @JsonProperty("school_id")
    private Long schoolId;

    @JsonProperty("assigned_bus_id")
    private Long assignedBusId;

    // Constructors
    public AdminAddDriverRequest() {}

    public AdminAddDriverRequest(String fullName, String email, String password, String phoneNumber, String licenseNumber, Long schoolId, Long assignedBusId) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.licenseNumber = licenseNumber;
        this.schoolId = schoolId;
        this.assignedBusId = assignedBusId;
    }

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public Long getAssignedBusId() { return assignedBusId; }
    public void setAssignedBusId(Long assignedBusId) { this.assignedBusId = assignedBusId; }
}