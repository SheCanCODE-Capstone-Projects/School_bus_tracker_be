package org.example.school_bus_tracker_be.Dtos.driver;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to create or update a driver")
public class DriverRequest {
    
    @NotBlank(message = "Full name is required")
    @Schema(description = "Driver full name", example = "John Smith")
    private String fullName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "Driver email", example = "john.smith@example.com")
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Schema(description = "Driver phone number", example = "+1234567890")
    private String phoneNumber;
    
    @NotBlank(message = "License number is required")
    @Schema(description = "Driver license number", example = "DL123456789")
    private String licenseNumber;
    
    @Schema(description = "Assigned bus ID", example = "1")
    private Long assignedBusId;
    
    public DriverRequest() {}
    
    public DriverRequest(String fullName, String email, String phoneNumber, String licenseNumber, Long assignedBusId) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.licenseNumber = licenseNumber;
        this.assignedBusId = assignedBusId;
    }
    
    // Getters and Setters
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
