package org.example.school_bus_tracker_be.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO class for student information
 * Contains basic student details for registration and updates
 */
public class StudentInfo {
    
    @NotBlank
    private String name;
    
    @NotNull
    private Integer age;
    
    @NotBlank
    private String gender;
    
    @NotBlank
    private String level;
    
    @NotBlank
    private String studentNumber;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }
}