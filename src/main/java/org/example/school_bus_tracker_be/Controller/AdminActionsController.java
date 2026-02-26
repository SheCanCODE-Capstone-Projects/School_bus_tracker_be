package org.example.school_bus_tracker_be.Controller;

import org.example.school_bus_tracker_be.Config.JwtTokenProvider;
import org.example.school_bus_tracker_be.DTO.AdminAddStudentRequest;
import org.example.school_bus_tracker_be.DTO.ApiResponse;
import org.example.school_bus_tracker_be.Dtos.bus.CreateBusRequest;
import org.example.school_bus_tracker_be.Dtos.bus.BusResponse;
import org.example.school_bus_tracker_be.Dtos.bus.UpdateBusRequest;
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
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/actions")
@PreAuthorize("hasRole('ADMIN')")
public class AdminActionsController {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final StudentBusRepository studentBusRepository;
    private final BusRepository busRepository;
    private final BusStopRepository busStopRepository;
    private final SchoolRepository schoolRepository;
    private final DriverRepository driverRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AdminActionsController(UserRepository userRepository, StudentRepository studentRepository,
                                StudentBusRepository studentBusRepository,
                                BusRepository busRepository, BusStopRepository busStopRepository,
                                SchoolRepository schoolRepository,
                                DriverRepository driverRepository, NotificationRepository notificationRepository,
                                PasswordResetTokenRepository passwordResetTokenRepository,
                                JwtTokenProvider jwtTokenProvider,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.studentBusRepository = studentBusRepository;
        this.busRepository = busRepository;
        this.busStopRepository = busStopRepository;
        this.schoolRepository = schoolRepository;
        this.driverRepository = driverRepository;
        this.notificationRepository = notificationRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
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
                bsInfo = new StudentResponse.BusStopInfo(saved.getBusStop().getId(), saved.getBusStop().getName(), null);
            }

            StudentResponse.AssignedBusInfo abInfo = null;
            if (saved.getAssignedBus() != null) {
                abInfo = new StudentResponse.AssignedBusInfo(saved.getAssignedBus().getId(), saved.getAssignedBus().getBusName(), saved.getAssignedBus().getBusNumber());
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

    @PutMapping("/parents/{id}")
    public ResponseEntity<ApiResponse<User>> updateParent(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        try {
            User parent = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Parent not found"));
            if (!parent.getRole().equals(Role.PARENT)) {
                throw new RuntimeException("User is not a parent");
            }
            String oldPhone = parent.getPhone();
            if (request.getName() != null && !request.getName().isBlank()) parent.setName(request.getName().trim());
            if (request.getEmail() != null && !request.getEmail().isBlank()) {
                String newEmail = request.getEmail().trim();
                if (userRepository.findByEmail(newEmail).filter(u -> !u.getId().equals(id)).isPresent()) {
                    throw new RuntimeException("Email already in use by another user");
                }
                parent.setEmail(newEmail);
            }
            if (request.getPhone() != null && !request.getPhone().isBlank()) {
                String newPhone = request.getPhone().trim();
                if (userRepository.findByPhone(newPhone).filter(u -> !u.getId().equals(id)).isPresent()) {
                    throw new RuntimeException("Phone already in use by another user");
                }
                parent.setPhone(newPhone);
            }
            if (request.getHomeAddress() != null) {
                parent.setHomeAddress(request.getHomeAddress().trim().isEmpty() ? null : request.getHomeAddress().trim());
            }
            if (request.getSchoolId() != null) {
                parent.setSchool(schoolRepository.findById(request.getSchoolId())
                        .orElseThrow(() -> new RuntimeException("School not found")));
            }
            if (request.getPassword() != null && !request.getPassword().isBlank()) {
                parent.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            if (parent.getSchool() == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Parent has no school assigned"));
            }
            Long schoolId = parent.getSchool().getId();

            // Update children (students) first – so child name and assigned bus always apply (even when parent phone changed)
            // Match by either old or new parent phone so we don't skip children when phone was just updated
            if (request.getChildren() != null && !request.getChildren().isEmpty()) {
                for (ChildUpdateItem item : request.getChildren()) {
                    if (item.getStudentId() == null) continue;
                    Student student = studentRepository.findById(item.getStudentId()).orElse(null);
                    if (student == null) continue;
                    boolean sameSchool = student.getSchool() != null && schoolId.equals(student.getSchool().getId());
                    boolean isThisParentChild = sameSchool && (student.getParentPhone().equals(parent.getPhone()) || student.getParentPhone().equals(oldPhone));
                    if (!isThisParentChild) continue;
                    if (item.getStudentName() != null && !item.getStudentName().isBlank()) {
                        student.setStudentName(item.getStudentName().trim());
                    }
                    if (item.getAssignedBusId() != null && item.getAssignedBusId() > 0) {
                        Bus bus = busRepository.findById(item.getAssignedBusId()).orElse(null);
                        student.setAssignedBus(bus);
                    } else {
                        student.setAssignedBus(null);
                    }
                    studentRepository.save(student);
                }
            }

            userRepository.save(parent);

            // If parent phone changed, update all their children's parent_phone and parent_name so they stay linked
            if (request.getPhone() != null && !request.getPhone().isBlank() && !parent.getPhone().equals(oldPhone)) {
                List<Student> linked = studentRepository.findByParentPhoneAndSchoolId(oldPhone, schoolId);
                for (Student s : linked) {
                    s.setParentPhone(parent.getPhone());
                    if (request.getName() != null && !request.getName().isBlank()) s.setParentName(parent.getName());
                    studentRepository.save(s);
                }
            }

            return ResponseEntity.ok(ApiResponse.success("Parent updated successfully", parent));
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
            // 1. Remove all parent–bus associations (ParentBus)
            studentBusRepository.deleteByParent_Id(id);
            studentBusRepository.flush();
            // 2. Delete all children (students) linked by parent phone + school (removes their bus assignment too)
            List<Student> children = studentRepository.findByParentPhoneAndSchoolId(parent.getPhone(), parent.getSchool().getId());
            for (Student student : children) {
                studentRepository.delete(student);
            }
            studentRepository.flush();
            // 3. Delete notifications and password-reset tokens for this parent
            notificationRepository.deleteByUserId(id);
            passwordResetTokenRepository.deleteByUserId(id);
            notificationRepository.flush();
            // 4. Delete the parent (user)
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
    public ResponseEntity<ApiResponse<BusResponse>> updateBus(@PathVariable Long id, @RequestBody UpdateBusRequest request) {
        try {
            Bus bus = busRepository.findById(id).orElseThrow(() -> new RuntimeException("Bus not found"));

            if (request.getBusName() != null && !request.getBusName().isBlank()) bus.setBusName(request.getBusName().trim());
            if (request.getBusNumber() != null && !request.getBusNumber().isBlank()) bus.setBusNumber(request.getBusNumber().trim());
            if (request.getSchoolId() != null) {
                School school = schoolRepository.findById(request.getSchoolId())
                    .orElseThrow(() -> new RuntimeException("School not found"));
                bus.setSchool(school);
            }
            if (request.getCapacity() != null) bus.setCapacity(request.getCapacity());
            if (request.getRoute() != null) bus.setRoute(request.getRoute());
            if (request.getStatus() != null && !request.getStatus().isBlank()) {
                try {
                    bus.setStatus(Bus.Status.valueOf(request.getStatus().trim().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(ApiResponse.error("Invalid status. Use: ACTIVE, INACTIVE, MAINTENANCE, ON_ROUTE"));
                }
            }

            if (Boolean.TRUE.equals(request.getUnassignDriver()) && bus.getAssignedDriver() != null) {
                Driver prev = bus.getAssignedDriver();
                prev.setAssignedBus(null);
                driverRepository.save(prev);
                bus.setAssignedDriver(null);
            } else if (request.getAssignedDriverId() != null) {
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
            BusResponse busResponse = convertToBusResponse(updatedBus);
            return ResponseEntity.ok(ApiResponse.success("Bus updated successfully", busResponse));
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
        @JsonProperty("home_address")
        private String homeAddress;
        @JsonProperty("school_id")
        private Long schoolId;
        private String password;
        /** Optional: update child/student info (name, assigned bus) when editing parent. Send as "children" or "students". */
        @JsonAlias("students")
        private List<ChildUpdateItem> children;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getHomeAddress() { return homeAddress; }
        public void setHomeAddress(String homeAddress) { this.homeAddress = homeAddress; }
        public Long getSchoolId() { return schoolId; }
        public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public List<ChildUpdateItem> getChildren() { return children; }
        public void setChildren(List<ChildUpdateItem> children) { this.children = children; }
    }

    /** One child/student in the update-parent request. Accepts snake_case or camelCase from frontend. */
    public static class ChildUpdateItem {
        @JsonProperty("id")
        @JsonAlias("studentId")
        private Long studentId;
        @JsonProperty("student_name")
        @JsonAlias("studentName")
        private String studentName;
        @JsonProperty("assigned_bus_id")
        @JsonAlias("assignedBusId")
        private Long assignedBusId;

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public Long getAssignedBusId() { return assignedBusId; }
        public void setAssignedBusId(Long assignedBusId) { this.assignedBusId = assignedBusId; }
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