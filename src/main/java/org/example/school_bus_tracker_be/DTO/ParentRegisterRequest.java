package org.example.school_bus_tracker_be.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ParentRegisterRequest {

    @NotNull
    private Long schoolId;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String phone;

    /** Optional. Frontend may omit; backend uses empty string when creating students. */
    private String homeAddress;

    /** Default bus stop for all children when not provided per child. */
    private Long busStopId;

    @NotNull
    @JsonAlias("students")
    private List<ChildInfo> children;

    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getHomeAddress() { return homeAddress; }
    public void setHomeAddress(String homeAddress) { this.homeAddress = homeAddress; }

    public Long getBusStopId() { return busStopId; }
    public void setBusStopId(Long busStopId) { this.busStopId = busStopId; }
    /** Accept string from frontend (e.g. "1"). */
    public void setBusStopId(String s) {
        this.busStopId = (s == null || s.isBlank()) ? null : Long.parseLong(s.trim());
    }

    public List<ChildInfo> getChildren() { return children; }
    public void setChildren(List<ChildInfo> children) { this.children = children; }
}
