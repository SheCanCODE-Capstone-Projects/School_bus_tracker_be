package org.example.school_bus_tracker_be.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AdminAddStudentRequest {

    @NotBlank(message = "Student name is required")
    private String studentName;

    @NotNull(message = "Parent ID is required")
    private Long parentId;

    @NotBlank(message = "Grade is required")
    private String grade;

    private String address;

    // Constructors
    public AdminAddStudentRequest() {}

    public AdminAddStudentRequest(String studentName, Long parentId, String grade, String address) {
        this.studentName = studentName;
        this.parentId = parentId;
        this.grade = grade;
        this.address = address;
    }

    // Getters and Setters
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}