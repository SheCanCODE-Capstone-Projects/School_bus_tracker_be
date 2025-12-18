package org.example.school_bus_tracker_be.Dtos.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO representing a login request.
 *
 * <p>
 * Clients send a JSON payload containing an email and password to the
 * authentication endpoint. Bean validation is used to ensure both
 * properties are present and non-empty. Additional validation rules (e.g.
 * email format) can be added as needed.
 */
public class AuthRequest {
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    public AuthRequest() {}

    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
