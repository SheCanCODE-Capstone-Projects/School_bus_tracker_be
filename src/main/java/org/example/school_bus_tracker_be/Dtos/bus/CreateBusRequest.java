package org.example.school_bus_tracker_be.Dtos.bus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to create a new bus")
public class CreateBusRequest {
    
    @NotBlank(message = "Bus name is required")
    @Schema(description = "Bus name", example = "School Bus A")
    private String busName;
    
    @NotBlank(message = "Bus number is required")
    @Schema(description = "Bus number", example = "SB001")
    private String busNumber;
    
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Schema(description = "Bus capacity", example = "50")
    private Integer capacity;
    
    @Schema(description = "Bus route", example = "Route 1: Downtown - School")
    private String route;
    
    @Schema(description = "Bus status", example = "ACTIVE")
    private String status = "ACTIVE";
    
    public CreateBusRequest() {}
    
    public CreateBusRequest(String busName, String busNumber, Integer capacity, String route, String status) {
        this.busName = busName;
        this.busNumber = busNumber;
        this.capacity = capacity;
        this.route = route;
        this.status = status;
    }
    
    // Getters and Setters
    public String getBusName() { return busName; }
    public void setBusName(String busName) { this.busName = busName; }
    
    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }
    
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    
    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}