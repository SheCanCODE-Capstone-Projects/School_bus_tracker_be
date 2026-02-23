package org.example.school_bus_tracker_be.Controller;

import org.example.school_bus_tracker_be.Config.JwtTokenProvider;
import org.example.school_bus_tracker_be.DTO.SimpleApiResponse;
import org.example.school_bus_tracker_be.Dtos.emergency.*;
import org.example.school_bus_tracker_be.Model.Emergency;
import org.example.school_bus_tracker_be.Service.EmergencyService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

@RestController
public class EmergencyController {

    private final EmergencyService emergencyService;
    private final JwtTokenProvider jwtTokenProvider;

    public EmergencyController(EmergencyService emergencyService, JwtTokenProvider jwtTokenProvider) {
        this.emergencyService = emergencyService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // ==================== DRIVER ENDPOINTS ====================

    /**
     * POST /driver/emergencies
     * Driver reports an emergency.
     * Accepts either:
     * - application/json (e.g. from mobile): type, description, latitude, longitude (no voice file).
     * - multipart/form-data: type, description, latitude, longitude, optional voiceAudio file.
     */
    @PostMapping(value = "/api/driver/emergencies", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> reportEmergencyJson(
            @Valid @RequestBody ReportEmergencyJsonRequest jsonRequest,
            HttpServletRequest httpRequest) {
        try {
            Long driverId = getCurrentUserId(httpRequest);
            // At least description required when no voice file
            if (jsonRequest.getDescription() == null || jsonRequest.getDescription().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(SimpleApiResponse.error("Description is required when not sending a voice recording."));
            }
            ReportEmergencyRequest request = new ReportEmergencyRequest();
            request.setType(jsonRequest.getType());
            request.setDescription(jsonRequest.getDescription());
            request.setLatitude(jsonRequest.getLatitude());
            request.setLongitude(jsonRequest.getLongitude());
            request.setVoiceAudio(null);
            ReportEmergencyResponse response = emergencyService.reportEmergency(request, driverId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping(value = "/api/driver/emergencies", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> reportEmergencyMultipart(
            @ModelAttribute ReportEmergencyRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long driverId = getCurrentUserId(httpRequest);
            ReportEmergencyResponse response = emergencyService.reportEmergency(request, driverId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleApiResponse.error(e.getMessage()));
        }
    }

    /**
     * GET /driver/emergencies
     * Driver views their emergencies (filtered by assigned bus)
     */
    @GetMapping("/api/driver/emergencies")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<EmergencyResponse>> getDriverEmergencies(HttpServletRequest httpRequest) {
        try {
            Long driverId = getCurrentUserId(httpRequest);
            List<EmergencyResponse> emergencies = emergencyService.getDriverEmergencies(driverId);
            return ResponseEntity.ok(emergencies);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /driver/emergencies/{id}
     * Driver views emergency details. Driver is resolved from users table (role DRIVER); access allowed only if emergency.driver_id equals driver's user id.
     */
    @GetMapping("/api/driver/emergencies/{id}")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> getDriverEmergencyById(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            Long driverId = getCurrentUserId(httpRequest);
            EmergencyResponse emergency = emergencyService.getDriverEmergencyById(id, driverId);
            return ResponseEntity.ok(emergency);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleApiResponse.error(e.getMessage()));
        }
    }

    // ==================== ADMIN ENDPOINTS ====================

    /**
     * GET /admin/emergencies/stats
     * Admin views emergency statistics
     */
    @GetMapping("/api/admin/emergencies/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmergencyStatsResponse> getEmergencyStats() {
        try {
            EmergencyStatsResponse stats = emergencyService.getEmergencyStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /admin/emergencies
     * Admin views emergency list (optionally filtered by status)
     */
    @GetMapping("/api/admin/emergencies")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmergencyResponse>> getEmergencies(
            @RequestParam(required = false) String status) {
        try {
            Emergency.Status statusEnum = null;
            if (status != null && !status.isEmpty()) {
                try {
                    statusEnum = Emergency.Status.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().build();
                }
            }
            List<EmergencyResponse> emergencies = emergencyService.getEmergencies(statusEnum);
            return ResponseEntity.ok(emergencies);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /admin/emergencies/{id}
     * Admin views emergency details
     */
    @GetMapping("/api/admin/emergencies/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmergencyResponse> getAdminEmergencyById(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            Long adminId = getCurrentUserId(httpRequest);
            EmergencyResponse emergency = emergencyService.getEmergencyById(id, adminId, "ADMIN");
            return ResponseEntity.ok(emergency);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PATCH /admin/emergencies/{id}/resolve
     * Admin resolves an emergency
     */
    @PatchMapping("/api/admin/emergencies/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resolveEmergency(
            @PathVariable Long id,
            @Valid @RequestBody ResolveEmergencyRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long adminId = getCurrentUserId(httpRequest);
            emergencyService.resolveEmergency(id, request, adminId);
            return ResponseEntity.ok(SimpleApiResponse.success("Emergency resolved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleApiResponse.error(e.getMessage()));
        }
    }

    /**
     * GET /admin/drivers/{driverId}/emergencies
     * Admin gets all emergencies created by a specific driver (driverId = User id of the driver).
     */
    @GetMapping("/api/admin/drivers/{driverId}/emergencies")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDriverEmergenciesByAdmin(@PathVariable Long driverId) {
        try {
            List<EmergencyResponse> emergencies = emergencyService.getDriverEmergencies(driverId);
            return ResponseEntity.ok(emergencies);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleApiResponse.error(e.getMessage()));
        }
    }

    // ==================== PARENT ENDPOINTS ====================

    /**
     * GET /parent/emergencies
     * Parent views emergencies related to their children's buses
     */
    @GetMapping("/api/parent/emergencies")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<List<EmergencyResponse>> getParentEmergencies(HttpServletRequest httpRequest) {
        try {
            Long parentId = getCurrentUserId(httpRequest);
            List<EmergencyResponse> emergencies = emergencyService.getParentEmergencies(parentId);
            return ResponseEntity.ok(emergencies);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /parent/emergencies/{id}
     * Parent views emergency details
     */
    @GetMapping("/api/parent/emergencies/{id}")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<EmergencyResponse> getParentEmergencyById(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            Long parentId = getCurrentUserId(httpRequest);
            EmergencyResponse emergency = emergencyService.getParentEmergencyById(id, parentId);
            return ResponseEntity.ok(emergency);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==================== UTILITY METHODS ====================

    private Long getCurrentUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtTokenProvider.getUserIdFromToken(token);
        }
        throw new RuntimeException("No valid token found");
    }
}
