
package org.example.school_bus_tracker_be.Controller;

import jakarta.validation.Valid;
import org.example.school_bus_tracker_be.Dtos.auth.AuthRequest;
import org.example.school_bus_tracker_be.Dtos.auth.AuthResponse;
import org.example.school_bus_tracker_be.Dtos.auth.PasswordResetRequest;
import org.example.school_bus_tracker_be.Dtos.auth.PasswordResetConfirmRequest;
import org.example.school_bus_tracker_be.DTO.DriverRegisterRequest;
import org.example.school_bus_tracker_be.DTO.ParentRegisterRequest;
import org.example.school_bus_tracker_be.DTO.SimpleApiResponse;
import org.example.school_bus_tracker_be.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<?> getAuthResponse() {
        return ResponseEntity.ok("Authentication endpoint is working");
    }

    @PostMapping("/register/driver")
    public ResponseEntity<AuthResponse> registerDriver(@Valid @RequestBody DriverRegisterRequest request) {
        return ResponseEntity.ok(authService.registerDriver(request));
    }

    @PostMapping("/register/parent")
    public ResponseEntity<AuthResponse> registerParent(@Valid @RequestBody ParentRegisterRequest request) {
        return ResponseEntity.ok(authService.registerParent(request));
    }


    /**
     * Authenticate a user and return a JWT access token upon successful
     * authentication.
     *
     * @param request the login request containing email and password
     * @return a response entity containing the authentication response
     */
     @PostMapping("/login")
     public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
         AuthResponse response = authService.login(request);
         return ResponseEntity.ok(response);
 }

    /**
     * Request password reset for a user by email.
     * Sends a reset token to the user's email address.
     *
     * @param request the password reset request containing email
     * @return a response entity indicating success
     */
    @PostMapping("/password-reset/request")
    public ResponseEntity<SimpleApiResponse> requestPasswordReset(@RequestBody @Valid PasswordResetRequest request) {
        authService.requestPasswordReset(request);
        return ResponseEntity.ok(SimpleApiResponse.success("Password reset email sent successfully"));
    }

    /**
     * Confirm password reset using the token and new password.
     *
     * @param request the password reset confirmation request
     * @return a response entity indicating success
     */
    @PostMapping("/password-reset/confirm")
    public ResponseEntity<SimpleApiResponse> confirmPasswordReset(@RequestBody @Valid PasswordResetConfirmRequest request) {
        authService.confirmPasswordReset(request);
        return ResponseEntity.ok(SimpleApiResponse.success("Password reset successfully"));
    }
}