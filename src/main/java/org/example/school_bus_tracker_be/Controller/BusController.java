package org.example.school_bus_tracker_be.Controller;

import org.example.school_bus_tracker_be.DTO.ApiResponse;
import org.example.school_bus_tracker_be.DTO.UpdateBusStatusRequest;
import org.example.school_bus_tracker_be.Dtos.bus.CreateBusRequest;
import org.example.school_bus_tracker_be.Dtos.bus.BusResponse;
import org.example.school_bus_tracker_be.Dtos.bus.UpdateBusRequest;
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
    @Operation(summary = "Update bus", description = "Update an existing bus (Edit Bus form: bus name, bus number, school, assign driver, status)")
    @SuppressWarnings("null")
    public ResponseEntity<ApiResponse<BusResponse>> updateBus(
            @PathVariable Long id,
            @RequestBody UpdateBusRequest request) {
        try {
            Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bus not found"));

            if (request.getBusName() != null && !request.getBusName().isBlank()) {
                bus.setBusName(request.getBusName().trim());
            }
            if (request.getBusNumber() != null && !request.getBusNumber().isBlank()) {
                bus.setBusNumber(request.getBusNumber().trim());
            }
            if (request.getSchoolId() != null) {
                School school = schoolRepository.findById(request.getSchoolId())
                    .orElseThrow(() -> new RuntimeException("School not found"));
                bus.setSchool(school);
            }
            if (request.getCapacity() != null) {
                bus.setCapacity(request.getCapacity());
            }
            if (request.getRoute() != null) {
                bus.setRoute(request.getRoute());
            }
            if (request.getStatus() != null && !request.getStatus().isBlank()) {
                try {
                    bus.setStatus(Bus.Status.valueOf(request.getStatus().trim().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(
                        ApiResponse.error("Invalid status. Use: ACTIVE, INACTIVE, MAINTENANCE, ON_ROUTE"));
                }
            }

            // Unassign driver if requested
            if (Boolean.TRUE.equals(request.getUnassignDriver()) && bus.getAssignedDriver() != null) {
                Driver prev = bus.getAssignedDriver();
                prev.setAssignedBus(null);
                driverRepository.save(prev);
                bus.setAssignedDriver(null);
            }
            // Assign or reassign driver: assignedDriverId is Driver table id
            else if (request.getAssignedDriverId() != null) {
                Driver newDriver = driverRepository.findById(request.getAssignedDriverId())
                    .orElseThrow(() -> new RuntimeException("Driver not found with ID: " + request.getAssignedDriverId()));
                if (!newDriver.getSchool().getId().equals(bus.getSchool().getId())) {
                    return ResponseEntity.badRequest().body(ApiResponse.error("Driver and bus must belong to the same school"));
                }
                if (bus.getAssignedDriver() != null) {
                    Driver prev = bus.getAssignedDriver();
                    prev.setAssignedBus(null);
                    driverRepository.save(prev);
                }
                if (newDriver.getAssignedBus() != null && !newDriver.getAssignedBus().getId().equals(bus.getId())) {
                    Bus prevBus = newDriver.getAssignedBus();
                    prevBus.setAssignedDriver(null);
                    busRepository.save(prevBus);
                }
                bus.setAssignedDriver(newDriver);
                newDriver.setAssignedBus(bus);
                driverRepository.save(newDriver);
            }

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
