package org.example.school_bus_tracker_be.Controller;

import org.example.school_bus_tracker_be.DTO.ApiResponse;
import org.example.school_bus_tracker_be.DTO.UpdateBusStatusRequest;
import org.example.school_bus_tracker_be.Dtos.bus.CreateBusRequest;
import org.example.school_bus_tracker_be.Dtos.bus.BusResponse;
import org.example.school_bus_tracker_be.Model.Bus;
import org.example.school_bus_tracker_be.Model.Driver;
import org.example.school_bus_tracker_be.Model.School;
import org.example.school_bus_tracker_be.Repository.BusRepository;
import org.example.school_bus_tracker_be.Repository.DriverRepository;
import org.example.school_bus_tracker_be.Repository.SchoolRepository;
import org.example.school_bus_tracker_be.Repository.StudentRepository;
import org.example.school_bus_tracker_be.Model.Student;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/buses")
@Tag(name = "Bus Management", description = "APIs for managing buses")
public class BusController {

    private final BusRepository busRepository;
    private final SchoolRepository schoolRepository;
    private final DriverRepository driverRepository;
    private final StudentRepository studentRepository;

    public BusController(BusRepository busRepository, SchoolRepository schoolRepository,
                       DriverRepository driverRepository, StudentRepository studentRepository) {
        this.busRepository = busRepository;
        this.schoolRepository = schoolRepository;
        this.driverRepository = driverRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping
    @Operation(summary = "Get all buses", description = "Retrieve all buses")
    public ResponseEntity<ApiResponse<List<BusResponse>>> getAllBuses() {
        try {
            List<BusResponse> buses = busRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Buses retrieved successfully", buses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get bus by ID", description = "Retrieve a specific bus by their ID")
    @SuppressWarnings("null")
    public ResponseEntity<ApiResponse<BusResponse>> getBusById(@PathVariable Long id) {
        try {
            Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bus not found"));
            return ResponseEntity.ok(ApiResponse.success("Bus retrieved successfully", convertToResponse(bus)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "Create new bus", description = "Create a new bus")
    public ResponseEntity<ApiResponse<BusResponse>> createBus(
            @Valid @RequestBody CreateBusRequest request,
            @RequestParam Long schoolId) {
        try {
            School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new RuntimeException("School not found"));
            
            Bus bus = new Bus();
            bus.setSchool(school);
            bus.setBusName(request.getBusName());
            bus.setBusNumber(request.getBusNumber());
            bus.setCapacity(request.getCapacity());
            bus.setRoute(request.getRoute());
            
            // Set status
            try {
                bus.setStatus(Bus.Status.valueOf(request.getStatus()));
            } catch (IllegalArgumentException e) {
                bus.setStatus(Bus.Status.ACTIVE);
            }
            
            // Note: Driver assignment should be done via PATCH /api/admin/assign-bus-to-driver
            
            Bus savedBus = busRepository.save(bus);
            return ResponseEntity.ok(ApiResponse.success("Bus created successfully", convertToResponse(savedBus)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update bus", description = "Update an existing bus")
    @SuppressWarnings("null")
    public ResponseEntity<ApiResponse<BusResponse>> updateBus(
            @PathVariable Long id,
            @Valid @RequestBody CreateBusRequest request) {
        try {
            Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bus not found"));
            
            bus.setBusName(request.getBusName());
            bus.setBusNumber(request.getBusNumber());
            bus.setCapacity(request.getCapacity());
            bus.setRoute(request.getRoute());
            
            // Set status
            try {
                bus.setStatus(Bus.Status.valueOf(request.getStatus()));
            } catch (IllegalArgumentException e) {
                bus.setStatus(Bus.Status.ACTIVE);
            }
            
            // Note: Driver assignment should be done via PATCH /api/admin/assign-bus-to-driver
            // We don't remove existing driver assignment here - use the dedicated endpoint
            
            Bus updatedBus = busRepository.save(bus);
            return ResponseEntity.ok(ApiResponse.success("Bus updated successfully", convertToResponse(updatedBus)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update bus status", description = "Update only the status of a bus (e.g., ACTIVE, INACTIVE, MAINTENANCE, ON_ROUTE)")
    @SuppressWarnings("null")
    public ResponseEntity<ApiResponse<BusResponse>> updateBusStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBusStatusRequest request) {
        try {
            Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bus not found"));
            
            // Validate and set status
            try {
                Bus.Status newStatus = Bus.Status.valueOf(request.getStatus().toUpperCase());
                bus.setStatus(newStatus);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.error("Invalid status. Valid values are: ACTIVE, INACTIVE, MAINTENANCE, ON_ROUTE"));
            }
            
            Bus updatedBus = busRepository.save(bus);
            return ResponseEntity.ok(ApiResponse.success("Bus status updated successfully", convertToResponse(updatedBus)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete bus", description = "Delete a bus by their ID")
    @SuppressWarnings("null")
    public ResponseEntity<ApiResponse<String>> deleteBus(@PathVariable Long id) {
        try {
            Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bus not found"));
            
            // Unassign driver before deleting bus to avoid foreign key constraint violation
            if (bus.getAssignedDriver() != null) {
                Driver driver = bus.getAssignedDriver();
                driver.setAssignedBus(null);
                driverRepository.save(driver);
            }
            
            // Unassign students from bus to avoid foreign key constraint violation
            List<Student> students = studentRepository.findByAssignedBusId(id);
            for (Student student : students) {
                student.setAssignedBus(null);
                studentRepository.save(student);
            }
            
            // Note: Bus locations, tracking records, and emergencies are kept for audit/history purposes
            // They reference the bus but won't cause foreign key violations if we handle them properly
            // The database should have ON DELETE SET NULL or similar for these relationships
            
            busRepository.delete(bus);
            return ResponseEntity.ok(ApiResponse.success("Bus deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    private BusResponse convertToResponse(Bus bus) {
        BusResponse.DriverInfo driverInfo = null;
        if (bus.getAssignedDriver() != null) {
            driverInfo = new BusResponse.DriverInfo(
                bus.getAssignedDriver().getFullName(),
                bus.getAssignedDriver().getEmail(),
                bus.getAssignedDriver().getPhoneNumber(),
                bus.getAssignedDriver().getLicenseNumber()
            );
        }
        
        return new BusResponse(
            bus.getId(),
            bus.getBusName(),
            bus.getBusNumber(),
            bus.getCapacity(),
            bus.getRoute(),
            bus.getStatus() != null ? bus.getStatus().name() : "ACTIVE",
            driverInfo
        );
    }
}
