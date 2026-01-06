package org.example.school_bus_tracker_be.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class ParentRegisterWithStudentsRequest {
    
    @NotBlank
    private String name;
    
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    private String password;
    
    @NotBlank
    private String phone;
    
    @NotNull
    private String busStopId;
    
    @Valid
    @NotNull
    private List<StudentInfo> students;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getBusStopId() { return busStopId; }
    public void setBusStopId(String busStopId) { this.busStopId = busStopId; }

    public List<StudentInfo> getStudents() { return students; }
    public void setStudents(List<StudentInfo> students) { this.students = students; }
}