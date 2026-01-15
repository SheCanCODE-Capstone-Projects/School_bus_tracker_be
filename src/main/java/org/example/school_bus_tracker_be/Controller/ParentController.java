package org.example.school_bus_tracker_be.Controller;

import jakarta.validation.Valid;
import org.example.school_bus_tracker_be.DTO.*;
import org.example.school_bus_tracker_be.Dtos.auth.AuthResponse;
import org.example.school_bus_tracker_be.Service.ParentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ParentController {

    private final ParentService parentService;

    public ParentController(ParentService parentService) {
        this.parentService = parentService;
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
}
