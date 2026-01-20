package org.example.school_bus_tracker_be.Controller;

import org.example.school_bus_tracker_be.Config.JwtTokenProvider;
import org.example.school_bus_tracker_be.DTO.SimpleApiResponse;
import org.example.school_bus_tracker_be.Dtos.location.*;
import org.example.school_bus_tracker_be.Service.LocationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class TrackingController {

    private final LocationService locationService;
    private final JwtTokenProvider jwtTokenProvider;

    public TrackingController(LocationService locationService, JwtTokenProvider jwtTokenProvider) {
        this.locationService = locationService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // ==================== DRIVER ENDPOINTS ====================

    /**
     * POST /driver/tracking/start
     * Driver starts tracking
     */
    @PostMapping("/api/driver/tracking/start")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<SimpleApiResponse> startTracking(HttpServletRequest httpRequest) {
        try {
            Long driverId = getCurrentUserId(httpRequest);
            locationService.startTracking(driverId);
            return ResponseEntity.ok(SimpleApiResponse.success("Tracking started successfully"));
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : "Failed to start tracking";
            return ResponseEntity.badRequest().body(SimpleApiResponse.error(errorMessage));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleApiResponse.error("An unexpected error occurred: " + e.getMessage()));
        }
    }

    /**
     * POST /driver/tracking/location
     * Driver sends GPS location update
     */
    @PostMapping("/api/driver/tracking/location")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<SimpleApiResponse> updateLocation(
            @Valid @RequestBody LocationUpdateRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long driverId = getCurrentUserId(httpRequest);
            locationService.updateLocation(driverId, request);
            return ResponseEntity.ok(SimpleApiResponse.success("Location updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleApiResponse.error(e.getMessage()));
        }
    }

    /**
     * POST /driver/tracking/stop
     * Driver stops tracking
     */
    @PostMapping("/api/driver/tracking/stop")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<SimpleApiResponse> stopTracking(HttpServletRequest httpRequest) {
        try {
            Long driverId = getCurrentUserId(httpRequest);
            locationService.stopTracking(driverId);
            return ResponseEntity.ok(SimpleApiResponse.success("Tracking stopped successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleApiResponse.error(e.getMessage()));
        }
    }

    // ==================== PARENT ENDPOINTS ====================

    /**
     * GET /parent/buses/{busId}/location
     * Parent views live bus location
     */
    @GetMapping("/api/parent/buses/{busId}/location")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<LocationResponse> getBusLocation(
            @PathVariable Long busId,
            HttpServletRequest httpRequest) {
        try {
            Long parentId = getCurrentUserId(httpRequest);
            LocationResponse location = locationService.getBusLocation(busId, parentId);
            return ResponseEntity.ok(location);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==================== ADMIN ENDPOINTS ====================

    /**
     * GET /admin/buses/{busId}/tracking-status
     * Admin views tracking status
     */
    @GetMapping("/api/admin/buses/{busId}/tracking-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TrackingStatusResponse> getTrackingStatus(@PathVariable Long busId) {
        try {
            TrackingStatusResponse status = locationService.getTrackingStatus(busId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /admin/buses/{busId}/locations
     * Admin views GPS history (with optional date filters)
     */
    @GetMapping("/api/admin/buses/{busId}/locations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LocationResponse>> getLocationHistory(
            @PathVariable Long busId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        try {
            List<LocationResponse> locations = locationService.getLocationHistory(busId, from, to);
            return ResponseEntity.ok(locations);
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
