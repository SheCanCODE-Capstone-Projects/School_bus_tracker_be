package org.example.school_bus_tracker_be.serviceimpl;

import org.example.school_bus_tracker_be.Config.JwtTokenProvider;
import org.example.school_bus_tracker_be.DTO.DriverRegisterRequest;
import org.example.school_bus_tracker_be.DTO.ParentRegisterRequest;
import org.example.school_bus_tracker_be.DTO.AdminRegisterRequest;
import org.example.school_bus_tracker_be.DTO.AdminAddDriverRequest;
import org.example.school_bus_tracker_be.DTO.AdminAddStudentRequest;
import org.example.school_bus_tracker_be.Dtos.auth.AuthRequest;
import org.example.school_bus_tracker_be.Dtos.auth.AuthResponse;
import org.example.school_bus_tracker_be.Model.School;
import org.example.school_bus_tracker_be.Model.User;
import org.example.school_bus_tracker_be.Model.Student;
import org.example.school_bus_tracker_be.Model.Driver;
import org.example.school_bus_tracker_be.Model.Bus;
import org.example.school_bus_tracker_be.Model.BusStop;
import org.example.school_bus_tracker_be.Repository.SchoolRepository;
import org.example.school_bus_tracker_be.Repository.UserRepository;
import org.example.school_bus_tracker_be.Repository.StudentRepository;
import org.example.school_bus_tracker_be.Repository.DriverRepository;
import org.example.school_bus_tracker_be.Repository.BusRepository;
import org.example.school_bus_tracker_be.Repository.BusStopRepository;
import org.example.school_bus_tracker_be.Service.AuthService;
import org.example.school_bus_tracker_be.Enum.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final StudentRepository studentRepository;
    private final DriverRepository driverRepository;
    private final BusRepository busRepository;
    private final BusStopRepository busStopRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            SchoolRepository schoolRepository,
            StudentRepository studentRepository,
            DriverRepository driverRepository,
            BusRepository busRepository,
            BusStopRepository busStopRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.schoolRepository = schoolRepository;
        this.studentRepository = studentRepository;
        this.driverRepository = driverRepository;
        this.busRepository = busRepository;
        this.busStopRepository = busStopRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    // LOGIN
    @Override
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + request.getEmail()));

        String token = tokenProvider.generateToken(user);
        return new AuthResponse(token, tokenProvider.getJwtExpirationMs(), user.getRole().name());
    }

    // REGISTER DRIVER
    @Override
    public AuthResponse registerDriver(DriverRegisterRequest request) {
        validateUser(request.getEmail(), request.getPhone());

        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new RuntimeException("School not found"));

        User driver = new User(
                school,
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhone(),
                Role.DRIVER
        );

        userRepository.save(driver);

        String token = tokenProvider.generateToken(driver);
        return new AuthResponse(token, tokenProvider.getJwtExpirationMs(), driver.getRole().name());
    }

    // REGISTER PARENT
    @Override
    public AuthResponse registerParent(ParentRegisterRequest request) {
        validateUser(request.getEmail(), request.getPhone());

        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new RuntimeException("School not found"));

        User parent = new User(
                school,
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhone(),
                Role.PARENT
        );

        userRepository.save(parent);

        String token = tokenProvider.generateToken(parent);
        return new AuthResponse(token, tokenProvider.getJwtExpirationMs(), parent.getRole().name());
    }

    // REGISTER ADMIN
    @Override
    public AuthResponse registerAdmin(AdminRegisterRequest request) {
        validateUser(request.getEmail(), request.getPhone());

        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new RuntimeException("School not found"));

        User admin = new User(
                school,
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhone(),
                Role.ADMIN
        );

        userRepository.save(admin);

        String token = tokenProvider.generateToken(admin);
        return new AuthResponse(token, tokenProvider.getJwtExpirationMs(), admin.getRole().name());
    }

    // ADMIN ADD DRIVER
    @Override
    public User addDriverByAdmin(AdminAddDriverRequest request, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Only admins can add drivers");
        }

        validateUser(request.getEmail(), request.getPhoneNumber());

        School school = admin.getSchool();
        if (request.getSchoolId() != null) {
            school = schoolRepository.findById(request.getSchoolId())
                    .orElseThrow(() -> new RuntimeException("School not found"));
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
        
        // Also create Driver entity
        Driver driver = new Driver(
                school,
                request.getFullName(),
                request.getEmail(),
                request.getPhoneNumber(),
                request.getLicenseNumber() != null ? request.getLicenseNumber() : "DL" + System.currentTimeMillis()
        );
        
        if (request.getAssignedBusId() != null) {
            Bus bus = busRepository.findById(request.getAssignedBusId())
                    .orElseThrow(() -> new RuntimeException("Bus not found"));
            driver.setAssignedBus(bus);
        }
        
        driverRepository.save(driver);

        return driverUser;
    }

    // ADMIN ADD STUDENT
    @Override
    public Student addStudentByAdmin(AdminAddStudentRequest request, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Only admins can add students");
        }

        School school = admin.getSchool();
        if (request.getSchoolId() != null) {
            school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new RuntimeException("School not found"));
        }

        Student student = new Student(
            school,
            request.getStudentName(),
            request.getAge(),
            request.getParentName(),
            request.getParentPhone(),
            request.getAddress()
        );
        
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

        return studentRepository.save(student);
    }

    @Override
    public void requestPasswordReset(org.example.school_bus_tracker_be.Dtos.auth.PasswordResetRequest request) {
        throw new RuntimeException("Password reset functionality not implemented");
    }

    @Override
    public void confirmPasswordReset(org.example.school_bus_tracker_be.Dtos.auth.PasswordResetConfirmRequest request) {
        throw new RuntimeException("Password reset functionality not implemented");
    }

    private void validateUser(String email, String phone) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByPhone(phone)) {
            throw new RuntimeException("Phone number already exists");
        }
    }
}