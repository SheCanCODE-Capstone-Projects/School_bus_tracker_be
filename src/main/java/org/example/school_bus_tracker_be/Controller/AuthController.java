package org.example.school_bus_tracker_be.Controller;

import org.example.school_bus_tracker_be.Config.JwtTokenProvider;
import org.example.school_bus_tracker_be.DTO.LoginRequest;
import org.example.school_bus_tracker_be.DTO.LoginResponse;
import org.example.school_bus_tracker_be.DTO.AdminRegisterRequest;
import org.example.school_bus_tracker_be.DTO.DriverRegisterRequest;
import org.example.school_bus_tracker_be.DTO.ParentRegisterRequest;
import org.example.school_bus_tracker_be.DTO.SimpleApiResponse;
import org.example.school_bus_tracker_be.Dtos.auth.AuthRequest;
import org.example.school_bus_tracker_be.Dtos.auth.AuthResponse;
import org.example.school_bus_tracker_be.Dtos.auth.PasswordResetRequest;
import org.example.school_bus_tracker_be.Dtos.auth.PasswordResetConfirmRequest;
import org.example.school_bus_tracker_be.Model.User;
import org.example.school_bus_tracker_be.Repository.UserRepository;
import org.example.school_bus_tracker_be.Service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthService authService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider tokenProvider,
                          AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<?> getAuthResponse() {
        return ResponseEntity.ok("Authentication endpoint is working");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user from database
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Generate JWT token
            String jwt = tokenProvider.generateToken(user);

            // Create login response (include user id for frontend)
            LoginResponse loginResponse = new LoginResponse(
                    user.getId(),
                    jwt,
                    "Bearer",
                    tokenProvider.getJwtExpirationMs(),
                    user.getEmail(),
                    user.getRole().name()
            );

            return ResponseEntity.ok(loginResponse);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }

    /**
     * Alternative login endpoint using AuthRequest DTO
     */
    @PostMapping("/login-alt")
    public ResponseEntity<AuthResponse> loginAlt(@RequestBody @Valid AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
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