package org.example.school_bus_tracker_be.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class AdminAddStudentRequest {

    @NotBlank(message = "Student name is required")
    private String studentName;

    @NotNull(message = "Age is required")
    @Min(value = 3, message = "Age must be at least 3")
    private Integer age;

    private String gender;

    @NotBlank(message = "Parent name is required")
    private String parentName;

    @NotBlank(message = "Parent phone is required")
    private String parentPhone;

    @NotBlank(message = "Address is required")
    private String address;

    private Long busStopId;

    private Long assignedBusId;
    
    @JsonProperty("school_id")
    private Long schoolId;

    // Constructors
    public AdminAddStudentRequest() {}

    public AdminAddStudentRequest(String studentName, Integer age, String parentName, String parentPhone, String address, Long busStopId, Long assignedBusId, Long schoolId) {
        this.studentName = studentName;
        this.age = age;
        this.parentName = parentName;
        this.parentPhone = parentPhone;
        this.address = address;
        this.busStopId = busStopId;
        this.assignedBusId = assignedBusId;
        this.schoolId = schoolId;
    }

    // Getters and Setters
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

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

    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
}