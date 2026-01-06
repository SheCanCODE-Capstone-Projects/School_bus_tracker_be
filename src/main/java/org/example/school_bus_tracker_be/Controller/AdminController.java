package org.example.school_bus_tracker_be.Controller;

import org.example.school_bus_tracker_be.Config.JwtTokenProvider;
import org.example.school_bus_tracker_be.DTO.AdminRegisterRequest;
import org.example.school_bus_tracker_be.DTO.AdminAddDriverRequest;
import org.example.school_bus_tracker_be.DTO.AdminAddStudentRequest;
import org.example.school_bus_tracker_be.DTO.ApiResponse;
import org.example.school_bus_tracker_be.Dtos.auth.AuthResponse;
import org.example.school_bus_tracker_be.Model.User;
import org.example.school_bus_tracker_be.Model.Student;
import org.example.school_bus_tracker_be.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    public AdminController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody AdminRegisterRequest request) {
        try {
            AuthResponse response = authService.registerAdmin(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/add-driver")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> addDriver(@Valid @RequestBody AdminAddDriverRequest request, HttpServletRequest httpRequest) {
        try {
            Long adminId = getCurrentUserId(httpRequest);
            User driver = authService.addDriverByAdmin(request, adminId);
            return ResponseEntity.ok(ApiResponse.success("Driver added successfully", driver));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/add-student")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Student>> addStudent(@Valid @RequestBody AdminAddStudentRequest request, HttpServletRequest httpRequest) {
        try {
            Long adminId = getCurrentUserId(httpRequest);
            Student student = authService.addStudentByAdmin(request, adminId);
            return ResponseEntity.ok(ApiResponse.success("Student added successfully", student));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtTokenProvider.getUserIdFromToken(token);
        }
        throw new RuntimeException("No valid token found");
    }
}