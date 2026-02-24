package org.example.school_bus_tracker_be.Controller;

import org.example.school_bus_tracker_be.Config.JwtTokenProvider;
import org.example.school_bus_tracker_be.DTO.AdminRegisterRequest;
import org.example.school_bus_tracker_be.DTO.AdminAddDriverRequest;
import org.example.school_bus_tracker_be.DTO.AdminAddParentRequest;
import org.example.school_bus_tracker_be.DTO.AdminAddStudentRequest;
import org.example.school_bus_tracker_be.DTO.ParentResponse;
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
import org.example.school_bus_tracker_be.Repository.DriverRepository;
import org.example.school_bus_tracker_be.Service.AuthService;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(AuthService authService, BusService busService, JwtTokenProvider jwtTokenProvider,
                          UserRepository userRepository, StudentRepository studentRepository,
                          DriverRepository driverRepository, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.busService = busService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.driverRepository = driverRepository;
        this.passwordEncoder = passwordEncoder;
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

    /**
     * PUT /api/admin/update-driver/{id}
     * Update driver by User id (users table, role DRIVER). Updates both User and Driver records.
     * Request body: email, name, phone_number, license_number (all optional).
     */
    @PutMapping("/update-driver/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DriverResponse>> updateDriver(
            @PathVariable Long id,
            @RequestBody AdminUpdateDriverRequest request,
            HttpServletRequest httpRequest) {
        try {
            User driverUser = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));
            if (!driverUser.getRole().equals(Role.DRIVER)) {
                throw new RuntimeException("User is not a driver");
            }
            Driver driver = driverRepository.findByEmail(driverUser.getEmail())
                    .orElseThrow(() -> new RuntimeException("Driver record not found for this user"));

            if (request.getName() != null && !request.getName().isBlank()) {
                driverUser.setName(request.getName().trim());
                driver.setFullName(request.getName().trim());
            }
            if (request.getEmail() != null && !request.getEmail().isBlank()) {
                String newEmail = request.getEmail().trim();
                if (!newEmail.equals(driverUser.getEmail())) {
                    if (userRepository.existsByEmail(newEmail)) {
                        throw new RuntimeException("Another user already has this email");
                    }
                    if (driverRepository.existsByEmail(newEmail)) {
                        throw new RuntimeException("Another driver already has this email");
                    }
                }
                driverUser.setEmail(newEmail);
                driver.setEmail(newEmail);
            }
            if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
                String newPhone = request.getPhoneNumber().trim();
                if (!newPhone.equals(driverUser.getPhone())) {
                    if (userRepository.existsByPhone(newPhone)) {
                        throw new RuntimeException("Another user already has this phone number");
                    }
                    if (driverRepository.existsByPhoneNumber(newPhone)) {
                        throw new RuntimeException("Another driver already has this phone number");
                    }
                }
                driverUser.setPhone(newPhone);
                driver.setPhoneNumber(newPhone);
            }
            if (request.getLicenseNumber() != null && !request.getLicenseNumber().isBlank()) {
                String newLicense = request.getLicenseNumber().trim();
                if (!newLicense.equals(driver.getLicenseNumber()) && driverRepository.existsByLicenseNumber(newLicense)) {
                    throw new RuntimeException("Another driver already has this license number");
                }
                driver.setLicenseNumber(newLicense);
            }
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                driverUser.setPassword(passwordEncoder.encode(request.getPassword()));
            }

            userRepository.save(driverUser);
            driverRepository.save(driver);
            return ResponseEntity.ok(ApiResponse.success("Driver updated successfully", convertToDriverResponse(driver)));
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

    @PostMapping("/add-parent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ParentResponse>> addParent(@Valid @RequestBody AdminAddParentRequest request, HttpServletRequest httpRequest) {
        try {
            Long adminId = getCurrentUserId(httpRequest);
            User parent = authService.addParentByAdmin(request, adminId);
            ParentResponse response = new ParentResponse(
                    parent.getId(),
                    parent.getName(),
                    parent.getEmail(),
                    parent.getPhone(),
                    parent.getSchool().getId(),
                    parent.getSchool().getName()
            );
            String message = (parent.getPassword() == null || parent.getPassword().isEmpty())
                    ? "Parent added successfully. They have no password yet and must use the password-reset flow to set one before first login."
                    : "Parent added successfully.";
            return ResponseEntity.ok(ApiResponse.success(message, response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
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
                
                // Convert students to StudentResponse with full details
                java.util.List<org.example.school_bus_tracker_be.Dtos.student.StudentResponse> studentResponses = students.stream()
                        .map(this::convertStudentToResponse)
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
    
    private org.example.school_bus_tracker_be.Dtos.student.StudentResponse convertStudentToResponse(Student student) {
        // Convert BusStop to BusStopInfo
        org.example.school_bus_tracker_be.Dtos.student.StudentResponse.BusStopInfo busStopInfo = null;
        if (student.getBusStop() != null) {
            String busStopAddress = student.getBusStop().getLatitude() + ", " + student.getBusStop().getLongitude();
            busStopInfo = new org.example.school_bus_tracker_be.Dtos.student.StudentResponse.BusStopInfo(
                    student.getBusStop().getId(),
                    student.getBusStop().getName(),
                    busStopAddress
            );
        }
        
        // Convert Bus to AssignedBusInfo
        org.example.school_bus_tracker_be.Dtos.student.StudentResponse.AssignedBusInfo busInfo = null;
        if (student.getAssignedBus() != null) {
            busInfo = new org.example.school_bus_tracker_be.Dtos.student.StudentResponse.AssignedBusInfo(
                    student.getAssignedBus().getId(),
                    student.getAssignedBus().getBusName(),
                    student.getAssignedBus().getBusNumber()
            );
        }
        
        // Create StudentResponse
        org.example.school_bus_tracker_be.Dtos.student.StudentResponse response = new org.example.school_bus_tracker_be.Dtos.student.StudentResponse(
                student.getId(),
                student.getStudentName(),
                student.getAge(),
                student.getParentName(),
                student.getParentPhone(),
                student.getAddress(),
                busStopInfo,
                busInfo
        );
        
        // Set school_id
        response.setSchoolId(student.getSchool().getId());
        
        return response;
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtTokenProvider.getUserIdFromToken(token);
        }
        throw new RuntimeException("No valid token found");
    }

    /** Request body for PUT /api/admin/update-driver/{id}. Supports snake_case (phone_number, license_number). */
    public static class AdminUpdateDriverRequest {
        private String name;
        private String email;
        @JsonProperty("phone_number")
        private String phoneNumber;
        @JsonProperty("license_number")
        private String licenseNumber;
        private String password;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getLicenseNumber() { return licenseNumber; }
        public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}