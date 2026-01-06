package org.example.school_bus_tracker_be.Dtos.driver;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Driver response with bus assignment details")
public class DriverResponse {
    
    @Schema(description = "Driver ID (auto-generated)", example = "1")
    private Long id;
    
    @JsonProperty("school_id")
    @Schema(description = "School ID the driver belongs to", example = "1")
    private Long schoolId;
    
    @JsonProperty("full_name")
    @Schema(description = "Driver full name", example = "John Smith")
    private String fullName;
    
    @Schema(description = "Driver email", example = "john.smith@example.com")
    private String email;
    
    @JsonProperty("phone_number")
    @Schema(description = "Driver phone number", example = "+1234567890")
    private String phoneNumber;
    
    @JsonProperty("license_number")
    @Schema(description = "Driver license number", example = "DL123456789")
    private String licenseNumber;
    
    @JsonProperty("assigned_bus_id")
    @Schema(description = "Assigned bus ID", example = "1")
    private Long assignedBusId;
    
    public DriverResponse() {}
    
    public DriverResponse(Long id, Long schoolId, String fullName, String email, String phoneNumber, String licenseNumber, Long assignedBusId) {
        this.id = id;
        this.schoolId = schoolId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.licenseNumber = licenseNumber;
        this.assignedBusId = assignedBusId;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    
    public Long getAssignedBusId() { return assignedBusId; }
    public void setAssignedBusId(Long assignedBusId) { this.assignedBusId = assignedBusId; }
}