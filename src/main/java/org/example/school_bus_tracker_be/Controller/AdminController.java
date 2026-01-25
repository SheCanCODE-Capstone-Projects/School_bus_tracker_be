package org.example.school_bus_tracker_be.Controller;

import org.example.school_bus_tracker_be.Config.JwtTokenProvider;
import org.example.school_bus_tracker_be.DTO.AdminRegisterRequest;
import org.example.school_bus_tracker_be.DTO.AdminAddDriverRequest;
import org.example.school_bus_tracker_be.DTO.AdminAddStudentRequest;
import org.example.school_bus_tracker_be.DTO.AssignBusToDriverRequest;
import org.example.school_bus_tracker_be.DTO.AssignStudentsToBusRequest;
import org.example.school_bus_tracker_be.DTO.ApiResponse;
import org.example.school_bus_tracker_be.DTO.StudentSimpleResponse;
import org.example.school_bus_tracker_be.DTO.ParentWithStudentsResponse;
import org.example.school_bus_tracker_be.Dtos.auth.AuthResponse;
import org.example.school_bus_tracker_be.Dtos.bus.BusResponse;
import org.example.school_bus_tracker_be.Dtos.driver.DriverResponse;
import org.example.school_bus_tracker_be.Model.Driver;
import org.example.school_bus_tracker_be.Model.User;
import org.example.school_bus_tracker_be.Model.Student;
import org.example.school_bus_tracker_be.Model.Bus;
import org.example.school_bus_tracker_be.Enum.Role;
import org.example.school_bus_tracker_be.Repository.UserRepository;
import org.example.school_bus_tracker_be.Repository.StudentRepository;
import org.example.school_bus_tracker_be.Service.AuthService;
import org.example.school_bus_tracker_be.Service.BusService;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AuthService authService;
    private final BusService busService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public AdminController(AuthService authService, BusService busService, JwtTokenProvider jwtTokenProvider,
                          UserRepository userRepository, StudentRepository studentRepository) {
        this.authService = authService;
        this.busService = busService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
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
    public ResponseEntity<ApiResponse<DriverResponse>> addDriver(@Valid @RequestBody AdminAddDriverRequest request, HttpServletRequest httpRequest) {
        try {
            Long adminId = getCurrentUserId(httpRequest);
            Driver driver = authService.addDriverByAdmin(request, adminId);
            DriverResponse driverResponse = convertToDriverResponse(driver);
            return ResponseEntity.ok(ApiResponse.success("Driver added successfully", driverResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    private DriverResponse convertToDriverResponse(Driver driver) {
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

    @PatchMapping("/assign-bus-to-driver")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BusResponse>> assignBusToDriver(@Valid @RequestBody AssignBusToDriverRequest request, HttpServletRequest httpRequest) {
        try {
            Long adminId = getCurrentUserId(httpRequest);
            Bus bus = busService.assignBusToDriver(request, adminId);
            BusResponse busResponse = convertToBusResponse(bus);
            return ResponseEntity.ok(ApiResponse.success("Bus assigned to driver successfully", busResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    private BusResponse convertToBusResponse(Bus bus) {
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

    @PatchMapping("/assign-students-to-bus")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<java.util.List<StudentSimpleResponse>>> assignStudentsToBus(@Valid @RequestBody AssignStudentsToBusRequest request, HttpServletRequest httpRequest) {
        try {
            Long adminId = getCurrentUserId(httpRequest);
            java.util.List<Student> students = busService.assignStudentsToBus(request, adminId);
            java.util.List<StudentSimpleResponse> studentResponses = students.stream()
                    .map(StudentSimpleResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Students assigned to bus successfully", studentResponses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/parents")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<java.util.List<ParentWithStudentsResponse>>> getAllParentsWithStudents(HttpServletRequest httpRequest) {
        try {
            Long adminId = getCurrentUserId(httpRequest);
            User admin = userRepository.findById(adminId)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));
            
            // Get all parents from the same school
            java.util.List<User> parents = userRepository.findBySchoolAndRole(admin.getSchool(), Role.PARENT);
            
            java.util.List<ParentWithStudentsResponse> parentsWithStudents = new ArrayList<>();
            
            for (User parent : parents) {
                // Get students for this parent
                java.util.List<Student> students = studentRepository.findByParentPhoneAndSchoolId(
                        parent.getPhone(),
                        parent.getSchool().getId()
                );
                
                // Convert students to StudentResponse
                java.util.List<org.example.school_bus_tracker_be.DTO.StudentResponse> studentResponses = students.stream()
                        .map(s -> new org.example.school_bus_tracker_be.DTO.StudentResponse(
                                s.getId(),
                                s.getStudentName(),
                                "ST" + String.format("%03d", s.getId()), // student number
                                s.getAge(),
                                calculateLevel(s.getAge()),
                                s.getGender() != null ? s.getGender().name() : "UNKNOWN"
                        ))
                        .collect(Collectors.toList());
                
                ParentWithStudentsResponse parentResponse = new ParentWithStudentsResponse(
                        parent.getId(),
                        parent.getName(),
                        parent.getEmail(),
                        parent.getPhone(),
                        parent.getSchool().getId(),
                        parent.getSchool().getName(),
                        studentResponses
                );
                
                parentsWithStudents.add(parentResponse);
            }
            
            return ResponseEntity.ok(ApiResponse.success("Parents with students retrieved successfully", parentsWithStudents));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    private String calculateLevel(Integer age) {
        if (age == null) return "UNKNOWN";
        if (age >= 18) return "S6";
        if (age >= 17) return "S5";
        if (age >= 16) return "S4";
        if (age >= 15) return "S3";
        if (age >= 14) return "S2";
        if (age >= 13) return "S1";
        if (age >= 12) return "P6";
        if (age >= 11) return "P5";
        if (age >= 10) return "P4";
        if (age >= 9) return "P3";
        if (age >= 8) return "P2";
        if (age >= 7) return "P1";
        return "NURSERY";
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