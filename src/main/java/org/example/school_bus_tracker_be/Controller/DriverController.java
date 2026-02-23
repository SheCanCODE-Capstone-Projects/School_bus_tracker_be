package org.example.school_bus_tracker_be.Controller;

import org.example.school_bus_tracker_be.DTO.ApiResponse;
import org.example.school_bus_tracker_be.Dtos.driver.DriverRequest;
import org.example.school_bus_tracker_be.Dtos.driver.DriverResponse;
import org.example.school_bus_tracker_be.Model.Driver;
import org.example.school_bus_tracker_be.Model.Bus;
import org.example.school_bus_tracker_be.Model.School;
import org.example.school_bus_tracker_be.Model.User;
import org.example.school_bus_tracker_be.Repository.DriverRepository;
import org.example.school_bus_tracker_be.Repository.BusRepository;
import org.example.school_bus_tracker_be.Repository.SchoolRepository;
import org.example.school_bus_tracker_be.Repository.UserRepository;
import org.example.school_bus_tracker_be.Enum.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/drivers")
@Tag(name = "Driver Management", description = "APIs for managing drivers")
public class DriverController {

    private final DriverRepository driverRepository;
    private final SchoolRepository schoolRepository;
    private final BusRepository busRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DriverController(DriverRepository driverRepository, SchoolRepository schoolRepository,
                          BusRepository busRepository, UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.driverRepository = driverRepository;
        this.schoolRepository = schoolRepository;
        this.busRepository = busRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @Operation(summary = "Get all drivers", description = "Retrieve all drivers")
    public ResponseEntity<ApiResponse<List<DriverResponse>>> getAllDrivers() {
        try {
            List<DriverResponse> drivers = driverRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Drivers retrieved successfully", drivers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get driver by ID", description = "Retrieve a driver by their User ID (from users table with role DRIVER)")
    @SuppressWarnings("null")
    public ResponseEntity<ApiResponse<DriverResponse>> getDriverById(@PathVariable Long id) {
        try {
            User driverUser = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));
            if (!driverUser.getRole().equals(Role.DRIVER)) {
                throw new RuntimeException("User is not a driver");
            }
            Driver driver = driverRepository.findByEmail(driverUser.getEmail())
                    .orElseThrow(() -> new RuntimeException("Driver record not found for this user"));
            return ResponseEntity.ok(ApiResponse.success("Driver retrieved successfully", convertToResponse(driver)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "Create new driver", description = "Create a new driver")
    public ResponseEntity<ApiResponse<DriverResponse>> createDriver(
            @Valid @RequestBody DriverRequest request) {
        try {
            // Validate required fields
            if (request.getSchoolId() == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("School ID is required"));
            }
            
            // Check if email already exists
            if (driverRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Email already exists"));
            }
            
            // Check if phone number already exists
            if (driverRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Phone number already exists"));
            }
            
            // Check if license number already exists
            if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
                return ResponseEntity.badRequest().body(ApiResponse.error("License number already exists"));
            }
            
            School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new RuntimeException("School not found"));
            
            // Create User entity if password is provided
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                if (userRepository.existsByEmail(request.getEmail())) {
                    return ResponseEntity.badRequest().body(ApiResponse.error("User with this email already exists"));
                }
                
                User driverUser = new User(
                    school,
                    request.getFullName(),
                    request.getEmail(),
                    passwordEncoder.encode(request.getPassword()),
                    request.getPhoneNumber(),
                    Role.DRIVER
                );
                userRepository.save(driverUser);
            }
            
            // Create Driver entity
            Driver driver = new Driver();
            driver.setSchool(school);
            driver.setFullName(request.getFullName());
            driver.setEmail(request.getEmail());
            driver.setPhoneNumber(request.getPhoneNumber());
            driver.setLicenseNumber(request.getLicenseNumber());
            
            if (request.getAssignedBusId() != null) {
                Bus bus = busRepository.findById(request.getAssignedBusId())
                    .orElseThrow(() -> new RuntimeException("Bus not found"));
                driver.setAssignedBus(bus);
            }
            
            Driver savedDriver = driverRepository.save(driver);
            return ResponseEntity.ok(ApiResponse.success("Driver created successfully", convertToResponse(savedDriver)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update driver", description = "Update an existing driver")
    public ResponseEntity<ApiResponse<DriverResponse>> updateDriver(
            @PathVariable Long id,
            @Valid @RequestBody DriverRequest request) {
        try {
            Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
            
            // Check if email already exists for another driver
            if (!driver.getEmail().equals(request.getEmail()) && driverRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Email already exists"));
            }
            
            // Check if phone number already exists for another driver
            if (!driver.getPhoneNumber().equals(request.getPhoneNumber()) && 
                driverRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Phone number already exists"));
            }
            
            // Check if license number already exists for another driver
            if (!driver.getLicenseNumber().equals(request.getLicenseNumber()) && 
                driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
                return ResponseEntity.badRequest().body(ApiResponse.error("License number already exists"));
            }
            
            // Update school if provided
            if (request.getSchoolId() != null && !driver.getSchool().getId().equals(request.getSchoolId())) {
                School school = schoolRepository.findById(request.getSchoolId())
                    .orElseThrow(() -> new RuntimeException("School not found"));
                driver.setSchool(school);
            }
            
            driver.setFullName(request.getFullName());
            driver.setEmail(request.getEmail());
            driver.setPhoneNumber(request.getPhoneNumber());
            driver.setLicenseNumber(request.getLicenseNumber());
            
            if (request.getAssignedBusId() != null) {
                Bus bus = busRepository.findById(request.getAssignedBusId())
                    .orElseThrow(() -> new RuntimeException("Bus not found"));
                driver.setAssignedBus(bus);
            } else {
                driver.setAssignedBus(null);
            }
            
            // Update User entity if password is provided
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                User driverUser = userRepository.findByEmail(driver.getEmail()).orElse(null);
                if (driverUser != null) {
                    driverUser.setPassword(passwordEncoder.encode(request.getPassword()));
                    if (request.getFullName() != null) driverUser.setName(request.getFullName());
                    if (request.getEmail() != null) driverUser.setEmail(request.getEmail());
                    if (request.getPhoneNumber() != null) driverUser.setPhone(request.getPhoneNumber());
                    userRepository.save(driverUser);
                }
            }
            
            Driver updatedDriver = driverRepository.save(driver);
            return ResponseEntity.ok(ApiResponse.success("Driver updated successfully", convertToResponse(updatedDriver)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete driver", description = "Delete a driver by their ID")
    public ResponseEntity<ApiResponse<String>> deleteDriver(@PathVariable Long id) {
        try {
            Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
            driverRepository.delete(driver);
            return ResponseEntity.ok(ApiResponse.success("Driver deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    private DriverResponse convertToResponse(Driver driver) {
        return new DriverResponse(
            driver.getId(),
            driver.getSchool().getId(),
            driver.getFullName(),
            driver.getEmail(),
            driver.getPhoneNumber(),
            driver.getLicenseNumber(),
            driver.getAssignedBus() != null ? driver.getAssignedBus().getId() : null
        );
    }
}
