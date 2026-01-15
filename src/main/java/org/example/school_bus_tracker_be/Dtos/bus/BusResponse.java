package org.example.school_bus_tracker_be.Dtos.bus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Bus response with driver details")
public class BusResponse {
    
    @Schema(description = "Bus ID", example = "1")
    private Long id;
    
    @Schema(description = "Bus name", example = "School Bus A")
    private String busName;
    
    @Schema(description = "Bus number", example = "SB001")
    private String busNumber;
    
    @Schema(description = "Bus capacity", example = "50")
    private Integer capacity;
    
    @Schema(description = "Bus route", example = "Route 1: Downtown - School")
    private String route;
    
    @Schema(description = "Assigned driver details")
    private DriverInfo assignedDriver;
    
    public BusResponse() {}
    
    public BusResponse(Long id, String busName, String busNumber, Integer capacity, String route, DriverInfo assignedDriver) {
        this.id = id;
        this.busName = busName;
        this.busNumber = busNumber;
        this.capacity = capacity;
        this.route = route;
        this.assignedDriver = assignedDriver;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getBusName() { return busName; }
    public void setBusName(String busName) { this.busName = busName; }
    
    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }
    
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    
    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }
    
    public DriverInfo getAssignedDriver() { return assignedDriver; }
    public void setAssignedDriver(DriverInfo assignedDriver) { this.assignedDriver = assignedDriver; }
    
    @Schema(description = "Driver information")
    public static class DriverInfo {
        @Schema(description = "Driver full name", example = "John Smith")
        private String fullName;
        
        @Schema(description = "Driver email", example = "john.smith@school.com")
        private String email;
        
        @Schema(description = "Driver phone number", example = "+1234567890")
        private String phoneNumber;
        
        @Schema(description = "Driver license number", example = "DL123456")
        private String licenseNumber;
        
        public DriverInfo() {}
        
        public DriverInfo(String fullName, String email, String phoneNumber, String licenseNumber) {
            this.fullName = fullName;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.licenseNumber = licenseNumber;
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
    }
}