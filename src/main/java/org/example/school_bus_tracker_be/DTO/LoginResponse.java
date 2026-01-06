package org.example.school_bus_tracker_be.DTO;

public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long expiresIn;
    private String email;
    private String role;

    // Constructors
    public LoginResponse() {}

    public LoginResponse(String token, String type, Long expiresIn, String email, String role) {
        this.token = token;
        this.type = type;
        this.expiresIn = expiresIn;
        this.email = email;
        this.role = role;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}