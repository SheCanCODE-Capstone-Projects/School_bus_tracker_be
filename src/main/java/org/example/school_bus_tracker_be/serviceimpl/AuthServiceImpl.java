package org.example.school_bus_tracker_be.serviceimpl;


import org.example.school_bus_tracker_be.Config.JwtTokenProvider;
import org.example.school_bus_tracker_be.DTO.ChildInfo;
import org.example.school_bus_tracker_be.DTO.DriverRegisterRequest;
import org.example.school_bus_tracker_be.DTO.ParentRegisterRequest;
import org.example.school_bus_tracker_be.Dtos.auth.AuthRequest;
import org.example.school_bus_tracker_be.Dtos.auth.AuthResponse;
import org.example.school_bus_tracker_be.Model.BusStop;
import org.example.school_bus_tracker_be.Model.School;
import org.example.school_bus_tracker_be.Model.Student;
import org.example.school_bus_tracker_be.Model.User;
import org.example.school_bus_tracker_be.Repository.BusStopRepository;
import org.example.school_bus_tracker_be.Repository.SchoolRepository;
import org.example.school_bus_tracker_be.Repository.StudentRepository;
import org.example.school_bus_tracker_be.Repository.UserRepository;
import org.example.school_bus_tracker_be.Service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final StudentRepository studentRepository;
    private final BusStopRepository busStopRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            SchoolRepository schoolRepository,
            StudentRepository studentRepository,
            BusStopRepository busStopRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.schoolRepository = schoolRepository;
        this.studentRepository = studentRepository;
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
                User.Role.DRIVER
        );

        userRepository.save(driver);

        String token = tokenProvider.generateToken(driver);
        return new AuthResponse(token, tokenProvider.getJwtExpirationMs(), driver.getRole().name());
    }

    // REGISTER PARENT
    @Override
    @Transactional
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
                request.getHomeAddress(),
                User.Role.PARENT
        );

        userRepository.save(parent);

        // Create children students
        try {
            for (ChildInfo child : request.getChildren()) {
                BusStop busStop = busStopRepository.findById(child.getBusStopId())
                        .orElse(null);
                
                Student student = new Student(
                        school,
                        parent,
                        busStop,
                        child.getFullName(),
                        child.getStudentNumber(),
                        child.getAge(),
                        calculateGrade(child.getAge())
                );
                studentRepository.save(student);
                System.out.println("Student saved: " + child.getFullName());
            }
        } catch (Exception e) {
            System.err.println("Error saving students: " + e.getMessage());
            e.printStackTrace();
        }

        String token = tokenProvider.generateToken(parent);
        return new AuthResponse(token, tokenProvider.getJwtExpirationMs(), parent.getRole().name());
    }

    private void validateUser(String email, String phone) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByPhone(phone)) {
            throw new RuntimeException("Phone number already exists");
        }
    }

    private Integer calculateGrade(Integer age) {
        return Math.max(1, age - 5);
    }
}
