package org.example.school_bus_tracker_be.Controller;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.example.school_bus_tracker_be.Config.JwtTokenProvider;
import org.example.school_bus_tracker_be.DTO.*;
import org.example.school_bus_tracker_be.Dtos.auth.AuthResponse;
import org.example.school_bus_tracker_be.Dtos.student.StudentResponse;
import org.example.school_bus_tracker_be.Enum.Role;
import org.example.school_bus_tracker_be.Model.User;
import org.example.school_bus_tracker_be.Model.Student;
import org.example.school_bus_tracker_be.Repository.NotificationRepository;
import org.example.school_bus_tracker_be.Repository.SchoolRepository;
import org.example.school_bus_tracker_be.Repository.StudentBusRepository;
import org.example.school_bus_tracker_be.Repository.StudentRepository;
import org.example.school_bus_tracker_be.Repository.UserRepository;
import org.example.school_bus_tracker_be.Service.NotificationService;
import org.example.school_bus_tracker_be.Service.ParentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ParentController {

    private final ParentService parentService;
    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final SchoolRepository schoolRepository;
    private final StudentRepository studentRepository;
    private final StudentBusRepository studentBusRepository;
    private final PasswordEncoder passwordEncoder;

    public ParentController(ParentService parentService, NotificationService notificationService,
                            JwtTokenProvider jwtTokenProvider, UserRepository userRepository,
                            NotificationRepository notificationRepository, SchoolRepository schoolRepository,
                            StudentRepository studentRepository, StudentBusRepository studentBusRepository,
                            PasswordEncoder passwordEncoder) {
        this.parentService = parentService;
        this.notificationService = notificationService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.schoolRepository = schoolRepository;
        this.studentRepository = studentRepository;
        this.studentBusRepository = studentBusRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth/register/parent")
    public ResponseEntity<AuthResponse> registerParent(@Valid @RequestBody ParentRegisterWithStudentsRequest request) {
        AuthResponse response = parentService.registerParentWithStudents(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/parent/{parentId}/students")
    public ResponseEntity<List<StudentResponse>> getParentStudents(@PathVariable Long parentId) {
        List<StudentResponse> students = parentService.getParentStudents(parentId);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/parent/bus/{busId}/location")
    public ResponseEntity<BusLocationResponse> getBusLocation(@PathVariable Long busId) {
        BusLocationResponse location = parentService.getBusLocation(busId);
        return ResponseEntity.ok(location);
    }

    @GetMapping("/parent/{parentId}/notifications")
    public ResponseEntity<List<NotificationResponse>> getParentNotifications(@PathVariable Long parentId) {
        List<NotificationResponse> notifications = parentService.getParentNotifications(parentId);
        return ResponseEntity.ok(notifications);
    }

    // ==================== NEW NOTIFICATIONS (DB-backed) ====================

    @GetMapping("/api/parent/notifications")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(HttpServletRequest request) {
        Long parentId = getCurrentUserId(request);
        return ResponseEntity.ok(notificationService.getUserNotifications(parentId));
    }

    @PatchMapping("/api/parent/notifications/{id}/read")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<NotificationResponse> markNotificationAsRead(@PathVariable Long id, HttpServletRequest request) {
        Long parentId = getCurrentUserId(request);
        return ResponseEntity.ok(notificationService.markAsRead(parentId, id));
    }

    /**
     * PUT /api/parent/profile – parent edits their own profile (name, email, phone, password).
     */
    @PutMapping("/api/parent/profile")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<ParentResponse>> updateMyProfile(
            @RequestBody UpdateParentProfileRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long parentId = getCurrentUserId(httpRequest);
            User parent = userRepository.findById(parentId).orElseThrow(() -> new RuntimeException("Parent not found"));
            if (!parent.getRole().equals(Role.PARENT)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("User is not a parent"));
            }
            if (request.getName() != null && !request.getName().isBlank()) parent.setName(request.getName().trim());
            if (request.getEmail() != null && !request.getEmail().isBlank()) {
                String newEmail = request.getEmail().trim();
                if (userRepository.findByEmail(newEmail).filter(u -> !u.getId().equals(parentId)).isPresent()) {
                    return ResponseEntity.badRequest().body(ApiResponse.error("Email already in use by another user"));
                }
                parent.setEmail(newEmail);
            }
            if (request.getPhone() != null && !request.getPhone().isBlank()) {
                String newPhone = request.getPhone().trim();
                if (userRepository.findByPhone(newPhone).filter(u -> !u.getId().equals(parentId)).isPresent()) {
                    return ResponseEntity.badRequest().body(ApiResponse.error("Phone already in use by another user"));
                }
                parent.setPhone(newPhone);
            }
            if (request.getHomeAddress() != null) {
                parent.setHomeAddress(request.getHomeAddress().trim().isEmpty() ? null : request.getHomeAddress().trim());
            }
            if (request.getSchoolId() != null) {
                parent.setSchool(schoolRepository.findById(request.getSchoolId())
                        .orElseThrow(() -> new RuntimeException("School not found")));
            }
            if (request.getPassword() != null && !request.getPassword().isBlank()) {
                parent.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            userRepository.save(parent);
            Long schoolId = parent.getSchool() != null ? parent.getSchool().getId() : null;
            String schoolName = parent.getSchool() != null ? parent.getSchool().getName() : null;
            ParentResponse response = new ParentResponse(
                    parent.getId(),
                    parent.getName(),
                    parent.getEmail(),
                    parent.getPhone(),
                    parent.getHomeAddress(),
                    schoolId,
                    schoolName
            );
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * DELETE /api/parent/profile – parent deletes their own account.
     */
    @DeleteMapping("/api/parent/profile")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<String>> deleteMyAccount(HttpServletRequest httpRequest) {
        try {
            Long parentId = getCurrentUserId(httpRequest);
            User parent = userRepository.findById(parentId).orElseThrow(() -> new RuntimeException("Parent not found"));
            if (!parent.getRole().equals(Role.PARENT)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("User is not a parent"));
            }
            if (parent.getSchool() == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Parent has no school assigned"));
            }
            // Remove parent–bus associations, delete children (and their bus assignments), then notifications, then user
            studentBusRepository.deleteByParent_Id(parentId);
            studentBusRepository.flush();
            List<Student> children = studentRepository.findByParentPhoneAndSchoolId(parent.getPhone(), parent.getSchool().getId());
            for (Student student : children) {
                studentRepository.delete(student);
            }
            studentRepository.flush();
            notificationRepository.deleteByUserId(parentId);
            notificationRepository.flush();
            userRepository.delete(parent);
            return ResponseEntity.ok(ApiResponse.success("Account deleted successfully", null));
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
