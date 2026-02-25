package org.example.school_bus_tracker_be.Dtos.driver;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Driver response with full bus details and students on the bus")
public class DriverDetailResponse {

    @Schema(description = "Driver ID (drivers table)", example = "1")
    private Long id;

    @JsonProperty("school_id")
    @Schema(description = "School ID the driver belongs to", example = "1")
    private Long schoolId;

    @JsonProperty("full_name")
    private String fullName;

    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("license_number")
    private String licenseNumber;

    @JsonProperty("assigned_bus_id")
    private Long assignedBusId;

    @JsonProperty("assigned_bus")
    @Schema(description = "Full bus information when driver has an assigned bus")
    private BusInfo assignedBus;

    @JsonProperty("students_on_bus")
    @Schema(description = "Students assigned to this driver's bus")
    private List<StudentOnBusInfo> studentsOnBus;

    public DriverDetailResponse() {}

    public DriverDetailResponse(Long id, Long schoolId, String fullName, String email, String phoneNumber,
                                String licenseNumber, Long assignedBusId, BusInfo assignedBus,
                                List<StudentOnBusInfo> studentsOnBus) {
        this.id = id;
        this.schoolId = schoolId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.licenseNumber = licenseNumber;
        this.assignedBusId = assignedBusId;
        this.assignedBus = assignedBus;
        this.studentsOnBus = studentsOnBus != null ? studentsOnBus : List.of();
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
    public BusInfo getAssignedBus() { return assignedBus; }
    public void setAssignedBus(BusInfo assignedBus) { this.assignedBus = assignedBus; }
    public List<StudentOnBusInfo> getStudentsOnBus() { return studentsOnBus; }
    public void setStudentsOnBus(List<StudentOnBusInfo> studentsOnBus) { this.studentsOnBus = studentsOnBus; }

    @Schema(description = "Bus information for driver's assigned bus")
    public static class BusInfo {
        private Long id;
        @JsonProperty("bus_name")
        private String busName;
        @JsonProperty("bus_number")
        private String busNumber;
        private String route;
        private Integer capacity;
        private String status;

        public BusInfo() {}

        public BusInfo(Long id, String busName, String busNumber, String route, Integer capacity, String status) {
            this.id = id;
            this.busName = busName;
            this.busNumber = busNumber;
            this.route = route;
            this.capacity = capacity;
            this.status = status;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getBusName() { return busName; }
        public void setBusName(String busName) { this.busName = busName; }
        public String getBusNumber() { return busNumber; }
        public void setBusNumber(String busNumber) { this.busNumber = busNumber; }
        public String getRoute() { return route; }
        public void setRoute(String route) { this.route = route; }
        public Integer getCapacity() { return capacity; }
        public void setCapacity(Integer capacity) { this.capacity = capacity; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    @Schema(description = "Student on the driver's bus (for driver to know who is on the bus)")
    public static class StudentOnBusInfo {
        private Long id;
        @JsonProperty("student_name")
        private String studentName;
        @JsonProperty("parent_name")
        private String parentName;
        @JsonProperty("parent_phone")
        private String parentPhone;
        private Integer age;

        public StudentOnBusInfo() {}

        public StudentOnBusInfo(Long id, String studentName, String parentName, String parentPhone, Integer age) {
            this.id = id;
            this.studentName = studentName;
            this.parentName = parentName;
            this.parentPhone = parentPhone;
            this.age = age;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getParentName() { return parentName; }
        public void setParentName(String parentName) { this.parentName = parentName; }
        public String getParentPhone() { return parentPhone; }
        public void setParentPhone(String parentPhone) { this.parentPhone = parentPhone; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
    }
}
