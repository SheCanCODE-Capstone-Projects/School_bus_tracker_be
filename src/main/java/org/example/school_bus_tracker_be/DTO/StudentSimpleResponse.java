package org.example.school_bus_tracker_be.DTO;

import org.example.school_bus_tracker_be.Model.Student;

public class StudentSimpleResponse {
    private Long id;
    private String studentName;
    private Integer age;
    private String gender;
    private String parentName;
    private String parentPhone;
    private String address;
    private Long busId;
    private String busNumber;

    public StudentSimpleResponse() {}

    public StudentSimpleResponse(Student student) {
        this.id = student.getId();
        this.studentName = student.getStudentName();
        this.age = student.getAge();
        this.gender = student.getGender() != null ? student.getGender().name() : null;
        this.parentName = student.getParentName();
        this.parentPhone = student.getParentPhone();
        this.address = student.getAddress();
        this.busId = student.getAssignedBus() != null ? student.getAssignedBus().getId() : null;
        this.busNumber = student.getAssignedBus() != null ? student.getAssignedBus().getBusNumber() : null;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Long getBusId() { return busId; }
    public void setBusId(Long busId) { this.busId = busId; }

    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }
}
