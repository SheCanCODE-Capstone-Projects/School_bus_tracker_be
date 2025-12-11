package org.example.school_bus_tracker_be.DTO;

public class AuthResponse {
    private String token;
    private String email;
    private String role;
    private Long schoolId;
    private String schoolName;

    public AuthResponse(String token, String email, String role, Long schoolId, String schoolName) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.schoolId = schoolId;
        this.schoolName = schoolName;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
}
