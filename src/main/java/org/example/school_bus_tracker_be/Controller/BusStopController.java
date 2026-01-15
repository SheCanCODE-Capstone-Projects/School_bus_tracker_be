package org.example.school_bus_tracker_be.Controller;

import org.example.school_bus_tracker_be.DTO.ApiResponse;
import org.example.school_bus_tracker_be.Dtos.bus.BusStopRequest;
import org.example.school_bus_tracker_be.Dtos.bus.BusStopResponse;
import org.example.school_bus_tracker_be.Model.BusStop;
import org.example.school_bus_tracker_be.Model.School;
import org.example.school_bus_tracker_be.Repository.BusStopRepository;
import org.example.school_bus_tracker_be.Repository.SchoolRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bus-stops")
@Tag(name = "Bus Stop Management", description = "APIs for managing bus stops")
public class BusStopController {

    private final BusStopRepository busStopRepository;
    private final SchoolRepository schoolRepository;

    public BusStopController(BusStopRepository busStopRepository, SchoolRepository schoolRepository) {
        this.busStopRepository = busStopRepository;
        this.schoolRepository = schoolRepository;
    }

    @PostMapping
    @Operation(summary = "Create new bus stop", description = "Create a new bus stop with auto-generated ID")
    public ResponseEntity<ApiResponse<BusStopResponse>> createBusStop(@Valid @RequestBody BusStopRequest request) {
        try {
            School school = schoolRepository.findById(request.getSchoolId())
                    .orElseThrow(() -> new RuntimeException("School not found"));

            BusStop busStop = new BusStop(
                    school,
                    request.getName(),
                    request.getLatitude(),
                    request.getLongitude()
            );

            BusStop savedBusStop = busStopRepository.save(busStop);

            BusStopResponse response = new BusStopResponse(
                    savedBusStop.getId(),
                    savedBusStop.getName()
            );

            return ResponseEntity.ok(ApiResponse.success("Bus stop created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Get all bus stops", description = "Retrieve all bus stops with their generated IDs")
    public ResponseEntity<ApiResponse<List<BusStopResponse>>> getAllBusStops() {
        try {
            List<BusStopResponse> busStops = busStopRepository.findAll().stream()
                    .map(busStop -> new BusStopResponse(
                            busStop.getId(),
                            busStop.getName()
                    ))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Bus stops retrieved successfully", busStops));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
