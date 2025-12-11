package org.example.school_bus_tracker_be.Service;

import org.example.school_bus_tracker_be.DTO.AuthResponse;
import org.example.school_bus_tracker_be.DTO.ChildInfo;
import org.example.school_bus_tracker_be.DTO.DriverRegisterRequest;
import org.example.school_bus_tracker_be.DTO.ParentRegisterRequest;
import org.example.school_bus_tracker_be.Model.BusStop;
import org.example.school_bus_tracker_be.Model.School;
import org.example.school_bus_tracker_be.Model.Student;
import org.example.school_bus_tracker_be.Model.User;
import org.example.school_bus_tracker_be.Repository.BusStopRepository;
import org.example.school_bus_tracker_be.Repository.SchoolRepository;
import org.example.school_bus_tracker_be.Repository.StudentRepository;
import org.example.school_bus_tracker_be.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final StudentRepository studentRepository;
    private final BusStopRepository busStopRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, SchoolRepository schoolRepository,
                      StudentRepository studentRepository, BusStopRepository busStopRepository,
                      PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.schoolRepository = schoolRepository;
        this.studentRepository = studentRepository;
        this.busStopRepository = busStopRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

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
                null,
                User.Role.DRIVER
        );

        userRepository.save(driver);
        String token = jwtService.generateToken(driver.getEmail(), driver.getRole().name());
        return new AuthResponse(token, driver.getEmail(), driver.getRole().name(), school.getId(), school.getName());
    }

    @Transactional
    public AuthResponse registerParent(ParentRegisterRequest request) {
        try {
            validateUser(request.getEmail(), request.getPhone());
            
            School school = schoolRepository.findById(request.getSchoolId())
                    .orElseThrow(() -> new RuntimeException("School not found"));

            // Create parent user first
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

            // Create children students (simplified - no bus stop for now)
            for (ChildInfo child : request.getChildren()) {
                Student student = new Student(
                        school,
                        parent,
                        null, // No bus stop for now
                        child.getFullName(),
                        child.getStudentNumber(),
                        child.getAge(),
                        calculateGrade(child.getAge())
                );
                studentRepository.save(student);
            }

            String token = jwtService.generateToken(parent.getEmail(), parent.getRole().name());
            return new AuthResponse(token, parent.getEmail(), parent.getRole().name(), school.getId(), school.getName());
        } catch (Exception e) {
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }
    }

    private Integer calculateGrade(Integer age) {
        return Math.max(1, age - 5);
    }

    private void validateUser(String email, String phone) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }
        if (userRepository.existsByPhone(phone)) {
            throw new RuntimeException("Phone number already exists: " + phone);
        }
    }
}
