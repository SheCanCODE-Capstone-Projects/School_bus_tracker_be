package org.example.school_bus_tracker_be.DTO;

public class StudentResponse {
    
    private Long id;
    private String name;
    private String studentNumber;
    private Integer age;
    private String level;
    private String gender;

    public StudentResponse() {}

    public StudentResponse(Long id, String name, String studentNumber, Integer age, String level, String gender) {
        this.id = id;
        this.name = name;
        this.studentNumber = studentNumber;
        this.age = age;
        this.level = level;
        this.gender = gender;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}