package org.example.school_bus_tracker_be.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChildInfo {

    @NotBlank
    @JsonAlias("name")
    private String fullName;

    @NotBlank
    private String studentNumber;

    @NotNull
    private Integer age;

    private String gender;

    /** Optional if parent request has busStopId (same for all children). */
    private Long busStopId;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Long getBusStopId() { return busStopId; }
    public void setBusStopId(Long busStopId) { this.busStopId = busStopId; }
}
