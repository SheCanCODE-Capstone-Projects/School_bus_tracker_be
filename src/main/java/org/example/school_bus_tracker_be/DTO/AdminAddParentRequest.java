package org.example.school_bus_tracker_be.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * Same shape as parent self-registration. Admin adds parent (and optionally their children).
 * Password is optional: if missing or empty, parent has no password and must use password-reset flow.
 * schoolId 0 or null = use admin's school.
 */
public class AdminAddParentRequest {

    /** 0 or null = use admin's school. */
    private Long schoolId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    /** Optional. If missing or empty, parent has no password and must use password-reset to set one. */
    private String password;

    @NotBlank(message = "Phone is required")
    private String phone;

    private String homeAddress;

    @Valid
    private List<ChildInfo> children;

    public AdminAddParentRequest() {}

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

    public List<ChildInfo> getChildren() { return children; }
    public void setChildren(List<ChildInfo> children) { this.children = children; }
}
