package org.example.school_bus_tracker_be.Dtos.student;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Schema(description = "Request to create or update a student")
public class StudentRequest {
    
    @NotBlank(message = "Student name is required")
    @Schema(description = "Student name", example = "Alice Johnson")
    private String studentName;
    
    @NotNull(message = "Age is required")
    @Min(value = 3, message = "Age must be at least 3")
    @Schema(description = "Student age", example = "12")
    private Integer age;
    
    @NotBlank(message = "Parent name is required")
    @Schema(description = "Parent name", example = "Robert Johnson")
    private String parentName;
    
    @NotBlank(message = "Parent phone is required")
    @Schema(description = "Parent phone", example = "+1234567890")
    private String parentPhone;
    
    @NotBlank(message = "Address is required")
    @Schema(description = "Student address", example = "123 Main St, City")
    private String address;
    
    @Schema(description = "Bus stop ID", example = "1")
    private Long busStopId;
    
    @Schema(description = "Assigned bus ID", example = "1")
    private Long assignedBusId;
    
    public StudentRequest() {}
    
    public StudentRequest(String studentName, Integer age, String parentName, String parentPhone, 
                         String address, Long busStopId, Long assignedBusId) {
        this.studentName = studentName;
        this.age = age;
        this.parentName = parentName;
        this.parentPhone = parentPhone;
        this.address = address;
        this.busStopId = busStopId;
        this.assignedBusId = assignedBusId;
    }
    
    // Getters and Setters
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    
    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }
    
    public String getParentPhone() { return parentPhone; }
    public void setParentPhone(String parentPhone) { this.parentPhone = parentPhone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public Long getBusStopId() { return busStopId; }
    public void setBusStopId(Long busStopId) { this.busStopId = busStopId; }
    
    public Long getAssignedBusId() { return assignedBusId; }
    public void setAssignedBusId(Long assignedBusId) { this.assignedBusId = assignedBusId; }
}