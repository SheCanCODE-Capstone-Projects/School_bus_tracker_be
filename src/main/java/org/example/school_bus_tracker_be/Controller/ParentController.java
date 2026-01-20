package org.example.school_bus_tracker_be.Controller;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.example.school_bus_tracker_be.Config.JwtTokenProvider;
import org.example.school_bus_tracker_be.DTO.*;
import org.example.school_bus_tracker_be.Dtos.auth.AuthResponse;
import org.example.school_bus_tracker_be.Service.NotificationService;
import org.example.school_bus_tracker_be.Service.ParentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ParentController {

    private final ParentService parentService;
    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;

    public ParentController(ParentService parentService, NotificationService notificationService, JwtTokenProvider jwtTokenProvider) {
        this.parentService = parentService;
        this.notificationService = notificationService;
        this.jwtTokenProvider = jwtTokenProvider;
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

    private Long getCurrentUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtTokenProvider.getUserIdFromToken(token);
        }
        throw new RuntimeException("No valid token found");
    }
}
