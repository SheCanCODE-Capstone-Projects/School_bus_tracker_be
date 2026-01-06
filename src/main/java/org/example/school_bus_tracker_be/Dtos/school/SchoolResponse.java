package org.example.school_bus_tracker_be.Dtos.school;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "School response with generated ID")
public class SchoolResponse {
    
    @Schema(description = "School ID", example = "1")
    private Long id;
    
    @Schema(description = "School name", example = "Green Valley High School")
    private String name;
    
    @Schema(description = "School address", example = "123 Education St, City")
    private String address;
    
    @Schema(description = "School phone", example = "+250788123456")
    private String phone;
    
    public SchoolResponse() {}
    
    public SchoolResponse(Long id, String name, String address, String phone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}