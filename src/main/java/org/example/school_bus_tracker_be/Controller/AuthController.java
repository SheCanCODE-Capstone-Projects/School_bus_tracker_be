
package org.example.school_bus_tracker_be.Controller;

import jakarta.validation.Valid;
import org.example.school_bus_tracker_be.DTO.AuthResponse;
import org.example.school_bus_tracker_be.DTO.DriverRegisterRequest;
import org.example.school_bus_tracker_be.DTO.ParentRegisterRequest;
import org.example.school_bus_tracker_be.Dtos.auth.AuthRequest;
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
//     @PostMapping("/login")
//     public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
//         AuthResponse response = authService.login(request);
//         return ResponseEntity.ok(response);
// }
}