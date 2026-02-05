package org.example.school_bus_tracker_be.Dtos.student;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Student response with bus assignment details")
public class StudentResponse {
    
    @Schema(description = "Student ID", example = "1")
    private Long id;
    
    @Schema(description = "Student name", example = "Alice Johnson")
    private String studentName;
    
    @Schema(description = "Student age", example = "12")
    private Integer age;
    
    @Schema(description = "Parent name", example = "Robert Johnson")
    private String parentName;
    
    @Schema(description = "Parent phone", example = "+1234567890")
    private String parentPhone;
    
    @Schema(description = "Student address", example = "123 Main St, City")
    private String address;
    
    @com.fasterxml.jackson.annotation.JsonProperty("school_id")
    @io.swagger.v3.oas.annotations.media.Schema(description = "School ID the student belongs to", example = "1")
    private Long schoolId;
    
    @Schema(description = "Bus stop information")
    private BusStopInfo busStop;
    
    @Schema(description = "Assigned bus information")
    private AssignedBusInfo assignedBus;
    
    public StudentResponse() {}
    
    public StudentResponse(Long id, String studentName, Integer age, String parentName, String parentPhone, 
                          String address, BusStopInfo busStop, AssignedBusInfo assignedBus) {
        this.id = id;
        this.studentName = studentName;
        this.age = age;
        this.parentName = parentName;
        this.parentPhone = parentPhone;
        this.address = address;
        this.busStop = busStop;
        this.assignedBus = assignedBus;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
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
    
    public BusStopInfo getBusStop() { return busStop; }
    public void setBusStop(BusStopInfo busStop) { this.busStop = busStop; }
    
    public AssignedBusInfo getAssignedBus() { return assignedBus; }
    public void setAssignedBus(AssignedBusInfo assignedBus) { this.assignedBus = assignedBus; }
    
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
    
    @Schema(description = "Bus stop information")
    public static class BusStopInfo {
        @Schema(description = "Bus stop ID", example = "1")
        private Long id;
        
        @Schema(description = "Bus stop name", example = "Main Street Stop")
        private String stopName;
        
        @Schema(description = "Bus stop address", example = "Main St & 1st Ave")
        private String address;
        
        public BusStopInfo() {}
        
        public BusStopInfo(Long id, String stopName, String address) {
            this.id = id;
            this.stopName = stopName;
            this.address = address;
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getStopName() { return stopName; }
        public void setStopName(String stopName) { this.stopName = stopName; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
    }
    
    @Schema(description = "Assigned bus information")
    public static class AssignedBusInfo {
        @Schema(description = "Bus ID", example = "1")
        private Long id;
        
        @Schema(description = "Bus name", example = "School Bus A")
        private String busName;
        
        @Schema(description = "Bus number", example = "SB001")
        private String busNumber;
        
        public AssignedBusInfo() {}
        
        public AssignedBusInfo(Long id, String busName, String busNumber) {
            this.id = id;
            this.busName = busName;
            this.busNumber = busNumber;
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getBusName() { return busName; }
        public void setBusName(String busName) { this.busName = busName; }
        
        public String getBusNumber() { return busNumber; }
        public void setBusNumber(String busNumber) { this.busNumber = busNumber; }
    }
}