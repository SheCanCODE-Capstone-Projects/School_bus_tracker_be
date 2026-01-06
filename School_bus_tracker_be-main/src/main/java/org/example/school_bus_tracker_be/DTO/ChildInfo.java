package org.example.school_bus_tracker_be.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ChildInfo {
    
    @NotBlank
    private String fullName;
    
    @NotBlank
    private String studentNumber;
    
    @NotNull
    private Integer age;
    
    @NotNull
    private Long busStopId;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Long getBusStopId() { return busStopId; }
    public void setBusStopId(Long busStopId) { this.busStopId = busStopId; }
}
