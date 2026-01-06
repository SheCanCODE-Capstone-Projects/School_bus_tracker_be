package org.example.school_bus_tracker_be.Dtos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to confirm password reset")
public class PasswordResetConfirmRequest {

    @NotBlank(message = "Token is required")
    @Schema(description = "Password reset token", example = "abc123def456", required = true)
    private String token;

    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Schema(description = "New password", example = "newPassword123", required = true, minLength = 6)
    private String newPassword;

    public PasswordResetConfirmRequest() {}

    public PasswordResetConfirmRequest(String token, String newPassword) {
        this.token = token;
        this.newPassword = newPassword;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
