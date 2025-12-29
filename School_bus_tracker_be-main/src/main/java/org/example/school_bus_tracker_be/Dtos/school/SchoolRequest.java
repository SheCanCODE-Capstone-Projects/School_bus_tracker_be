package org.example.school_bus_tracker_be.Dtos.school;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to create a new school")
public class SchoolRequest {
    
    @NotBlank(message = "School name is required")
    @Schema(description = "School name", example = "Green Valley High School", required = true)
    private String name;
    
    @NotBlank(message = "Address is required")
    @Schema(description = "School address", example = "123 Education St, City", required = true)
    private String address;
    
    @NotBlank(message = "Phone is required")
    @Schema(description = "School phone", example = "+250788123456", required = true)
    private String phone;
    
    public SchoolRequest() {}
    
    public SchoolRequest(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}