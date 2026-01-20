package org.example.school_bus_tracker_be.Controller;

import org.example.school_bus_tracker_be.Config.JwtTokenProvider;
import org.example.school_bus_tracker_be.DTO.AdminAddStudentRequest;
import org.example.school_bus_tracker_be.DTO.ApiResponse;
import org.example.school_bus_tracker_be.Dtos.bus.CreateBusRequest;
import org.example.school_bus_tracker_be.Dtos.student.StudentResponse;
import org.example.school_bus_tracker_be.Model.*;
import org.example.school_bus_tracker_be.Repository.*;
import org.example.school_bus_tracker_be.Enum.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import jakarta.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/actions")
@PreAuthorize("hasRole('ADMIN')")
public class AdminActionsController {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final BusRepository busRepository;
    private final BusStopRepository busStopRepository;
    private final SchoolRepository schoolRepository;
    private final DriverRepository driverRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AdminActionsController(UserRepository userRepository, StudentRepository studentRepository,
                                BusRepository busRepository, BusStopRepository busStopRepository,
                                SchoolRepository schoolRepository,
                                DriverRepository driverRepository, JwtTokenProvider jwtTokenProvider, 
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.busRepository = busRepository;
        this.busStopRepository = busStopRepository;
        this.schoolRepository = schoolRepository;
        this.driverRepository = driverRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    // DRIVER MANAGEMENT
    @GetMapping("/drivers")
    public ResponseEntity<ApiResponse<List<User>>> getAllDrivers(HttpServletRequest request) {
        try {
            Long adminId = getCurrentUserId(request);
            User admin = userRepository.findById(adminId).orElseThrow();
            List<User> drivers = userRepository.findBySchoolAndRole(admin.getSchool(), Role.DRIVER);
            return ResponseEntity.ok(ApiResponse.success("Drivers retrieved successfully", drivers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/drivers/{id}")
    public ResponseEntity<ApiResponse<User>> getDriver(@PathVariable Long id) {
        try {
            User driver = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Driver not found"));
            if (!driver.getRole().equals(Role.DRIVER)) {
                throw new RuntimeException("User is not a driver");
            }
            return ResponseEntity.ok(ApiResponse.success("Driver retrieved successfully", driver));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/drivers/{id}")
    public ResponseEntity<ApiResponse<User>> updateDriver(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        try {
            User driver = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Driver not found"));
            if (!driver.getRole().equals(Role.DRIVER)) {
                throw new RuntimeException("User is not a driver");
            }
            
            if (request.getName() != null) driver.setName(request.getName());
            if (request.getEmail() != null) driver.setEmail(request.getEmail());
            if (request.getPhone() != null) driver.setPhone(request.getPhone());
            if (request.getPassword() != null) driver.setPassword(passwordEncoder.encode(request.getPassword()));
            
            userRepository.save(driver);
            return ResponseEntity.ok(ApiResponse.success("Driver updated successfully", driver));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/drivers/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDriver(@PathVariable Long id) {
        try {
            User driver = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Driver not found"));
            if (!driver.getRole().equals(Role.DRIVER)) {
                throw new RuntimeException("User is not a driver");
            }
            userRepository.delete(driver);
            return ResponseEntity.ok(ApiResponse.success("Driver deleted successfully", "Driver removed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // STUDENT MANAGEMENT
    @PostMapping("/students")
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(@Valid @RequestBody AdminAddStudentRequest request, HttpServletRequest httpRequest) {
        try {
            Long adminId = getCurrentUserId(httpRequest);
            User admin = userRepository.findById(adminId).orElseThrow();
            
            Student student = new Student();
            if (request.getSchoolId() != null) {
                School school = schoolRepository.findById(request.getSchoolId())
                        .orElseThrow(() -> new RuntimeException("School not found"));
                student.setSchool(school);
            } else {
                student.setSchool(admin.getSchool());
            }
            student.setStudentName(request.getStudentName());
            student.setAge(request.getAge());
            student.setParentName(request.getParentName());
            student.setParentPhone(request.getParentPhone());
            student.setAddress(request.getAddress());
            
            // Set bus stop if provided
            if (request.getBusStopId() != null) {
                BusStop busStop = busStopRepository.findById(request.getBusStopId())
                    .orElseThrow(() -> new RuntimeException("Bus stop not found"));
                student.setBusStop(busStop);
            }
            
            // Set assigned bus if provided
            if (request.getAssignedBusId() != null) {
                Bus bus = busRepository.findById(request.getAssignedBusId())
                    .orElseThrow(() -> new RuntimeException("Bus not found"));
                student.setAssignedBus(bus);
            }
            
            Student saved = studentRepository.save(student);

            StudentResponse.BusStopInfo bsInfo = null;
            if (saved.getBusStop() != null) {
                bsInfo = new StudentResponse.BusStopInfo(saved.getBusStop().getName(), null);
            }

            StudentResponse.AssignedBusInfo abInfo = null;
            if (saved.getAssignedBus() != null) {
                abInfo = new StudentResponse.AssignedBusInfo(saved.getAssignedBus().getBusName(), saved.getAssignedBus().getBusNumber());
            }

            StudentResponse resp = new StudentResponse(
                    saved.getId(),
                    saved.getStudentName(),
                    saved.getAge(),
                    saved.getParentName(),
                    saved.getParentPhone(),
                    saved.getAddress(),
                    bsInfo,
                    abInfo
            );

            // set school_id in response if needed
            resp.setSchoolId(saved.getSchool() != null ? saved.getSchool().getId() : null);

            return ResponseEntity.ok(ApiResponse.success("Student created successfully", resp));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/students")
    public ResponseEntity<ApiResponse<List<Student>>> getAllStudents(HttpServletRequest request) {
        try {
            Long adminId = getCurrentUserId(request);
            User admin = userRepository.findById(adminId).orElseThrow();
            List<Student> students = studentRepository.findBySchool(admin.getSchool());
            return ResponseEntity.ok(ApiResponse.success("Students retrieved successfully", students));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<ApiResponse<Student>> getStudent(@PathVariable Long id) {
        try {
            Student student = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));
            return ResponseEntity.ok(ApiResponse.success("Student retrieved successfully", student));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<ApiResponse<Student>> updateStudent(@PathVariable Long id, @RequestBody UpdateStudentRequest request) {
        try {
            Student student = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));
            
            if (request.getStudentName() != null) student.setStudentName(request.getStudentName());
            if (request.getSchoolId() != null) {
                School school = schoolRepository.findById(request.getSchoolId())
                        .orElseThrow(() -> new RuntimeException("School not found"));
                student.setSchool(school);
            }
            if (request.getAddress() != null) student.setAddress(request.getAddress());
            if (request.getAge() != null) student.setAge(request.getAge());
            if (request.getParentName() != null) student.setParentName(request.getParentName());
            if (request.getParentPhone() != null) student.setParentPhone(request.getParentPhone());
            
            studentRepository.save(student);
            return ResponseEntity.ok(ApiResponse.success("Student updated successfully", student));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<ApiResponse<String>> deleteStudent(@PathVariable Long id) {
        try {
            Student student = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));
            studentRepository.delete(student);
            return ResponseEntity.ok(ApiResponse.success("Student deleted successfully", "Student removed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // PARENT MANAGEMENT
    @GetMapping("/parents")
    public ResponseEntity<ApiResponse<List<User>>> getAllParents(HttpServletRequest request) {
        try {
            Long adminId = getCurrentUserId(request);
            User admin = userRepository.findById(adminId).orElseThrow();
            List<User> parents = userRepository.findBySchoolAndRole(admin.getSchool(), Role.PARENT);
            return ResponseEntity.ok(ApiResponse.success("Parents retrieved successfully", parents));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/parents/{id}")
    public ResponseEntity<ApiResponse<String>> deleteParent(@PathVariable Long id) {
        try {
            User parent = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Parent not found"));
            if (!parent.getRole().equals(Role.PARENT)) {
                throw new RuntimeException("User is not a parent");
            }
            userRepository.delete(parent);
            return ResponseEntity.ok(ApiResponse.success("Parent deleted successfully", "Parent removed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // BUS MANAGEMENT
    @GetMapping("/buses")
    public ResponseEntity<ApiResponse<List<Bus>>> getAllBuses(HttpServletRequest request) {
        try {
            Long adminId = getCurrentUserId(request);
            User admin = userRepository.findById(adminId).orElseThrow();
            List<Bus> buses = busRepository.findBySchool(admin.getSchool());
            return ResponseEntity.ok(ApiResponse.success("Buses retrieved successfully", buses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/buses")
    public ResponseEntity<ApiResponse<Bus>> createBus(@Valid @RequestBody CreateBusRequest request, HttpServletRequest httpRequest) {
        try {
            Long adminId = getCurrentUserId(httpRequest);
            User admin = userRepository.findById(adminId).orElseThrow();
            
            Bus bus = new Bus();
            bus.setBusName(request.getBusName());
            bus.setBusNumber(request.getBusNumber());
            bus.setCapacity(request.getCapacity());
            bus.setRoute(request.getRoute());
            bus.setSchool(admin.getSchool());
            
            // Set status
            if (request.getStatus() != null) {
                bus.setStatus(Bus.Status.valueOf(request.getStatus().toUpperCase()));
            } else {
                bus.setStatus(Bus.Status.ACTIVE);
            }
            
            // Note: Driver assignment should be done via PATCH /api/admin/assign-bus-to-driver
            
            busRepository.save(bus);
            return ResponseEntity.ok(ApiResponse.success("Bus created successfully", bus));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/buses/{id}")
    public ResponseEntity<ApiResponse<Bus>> updateBus(@PathVariable Long id, @RequestBody CreateBusRequest request) {
        try {
            Bus bus = busRepository.findById(id).orElseThrow(() -> new RuntimeException("Bus not found"));
            
            if (request.getBusName() != null) bus.setBusName(request.getBusName());
            if (request.getBusNumber() != null) bus.setBusNumber(request.getBusNumber());
            if (request.getCapacity() != null) bus.setCapacity(request.getCapacity());
            if (request.getRoute() != null) bus.setRoute(request.getRoute());
            
            if (request.getStatus() != null) {
                bus.setStatus(Bus.Status.valueOf(request.getStatus().toUpperCase()));
            }
            
            // Note: Driver assignment should be done via PATCH /api/admin/assign-bus-to-driver
            
            busRepository.save(bus);
            return ResponseEntity.ok(ApiResponse.success("Bus updated successfully", bus));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/buses/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBus(@PathVariable Long id) {
        try {
            Bus bus = busRepository.findById(id).orElseThrow(() -> new RuntimeException("Bus not found"));
            
            // Unassign driver before deleting bus to avoid foreign key constraint violation
            if (bus.getAssignedDriver() != null) {
                Driver driver = bus.getAssignedDriver();
                driver.setAssignedBus(null);
                driverRepository.save(driver);
            }
            
            busRepository.delete(bus);
            return ResponseEntity.ok(ApiResponse.success("Bus deleted successfully", "Bus removed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // SYSTEM STATS
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<SystemStats>> getSystemStats(HttpServletRequest request) {
        try {
            Long adminId = getCurrentUserId(request);
            User admin = userRepository.findById(adminId).orElseThrow();
            School school = admin.getSchool();
            
            SystemStats stats = new SystemStats();
            stats.setTotalDrivers(userRepository.countBySchoolAndRole(school, Role.DRIVER));
            stats.setTotalParents(userRepository.countBySchoolAndRole(school, Role.PARENT));
            stats.setTotalStudents(studentRepository.countBySchool(school));
            stats.setTotalBuses(busRepository.countBySchool(school));
            
            return ResponseEntity.ok(ApiResponse.success("Stats retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtTokenProvider.getUserIdFromToken(token);
        }
        throw new RuntimeException("No valid token found");
    }

    // DTOs
    public static class UpdateUserRequest {
        private String name;
        private String email;
        private String phone;
        private String password;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class UpdateStudentRequest {
        private String studentName;
        private Integer age;
        private String address;
        private String parentName;
        private String parentPhone;

        @JsonProperty("school_id")
        private Long schoolId;

        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getParentName() { return parentName; }
        public void setParentName(String parentName) { this.parentName = parentName; }
        public String getParentPhone() { return parentPhone; }
        public void setParentPhone(String parentPhone) { this.parentPhone = parentPhone; }
        public Long getSchoolId() { return schoolId; }
        public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
    }

    public static class SystemStats {
        private long totalDrivers;
        private long totalParents;
        private long totalStudents;
        private long totalBuses;

        public long getTotalDrivers() { return totalDrivers; }
        public void setTotalDrivers(long totalDrivers) { this.totalDrivers = totalDrivers; }
        public long getTotalParents() { return totalParents; }
        public void setTotalParents(long totalParents) { this.totalParents = totalParents; }
        public long getTotalStudents() { return totalStudents; }
        public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }
        public long getTotalBuses() { return totalBuses; }
        public void setTotalBuses(long totalBuses) { this.totalBuses = totalBuses; }
    }
}