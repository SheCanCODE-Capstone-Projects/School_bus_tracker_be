package org.example.school_bus_tracker_be.Dtos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to confirm password reset")
public class PasswordResetConfirmRequest {

    @NotBlank(message = "Verification code is required")
    @Schema(description = "6-digit verification code sent to email", example = "123456", required = true)
    private String code;

    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Schema(description = "New password", example = "newPassword123", required = true, minLength = 6)
    private String newPassword;

    @NotBlank(message = "Password confirmation is required")
    @Schema(description = "Confirm new password", example = "newPassword123", required = true)
    private String confirmPassword;

    public PasswordResetConfirmRequest() {}

    public PasswordResetConfirmRequest(String code, String newPassword, String confirmPassword) {
        this.code = code;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
