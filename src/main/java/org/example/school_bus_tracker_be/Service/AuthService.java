package org.example.school_bus_tracker_be.Service;

import org.example.school_bus_tracker_be.DTO.AuthResponse;
import org.example.school_bus_tracker_be.DTO.DriverRegisterRequest;
import org.example.school_bus_tracker_be.DTO.ParentRegisterRequest;
import org.example.school_bus_tracker_be.Model.School;
import org.example.school_bus_tracker_be.Model.User;
import org.example.school_bus_tracker_be.Repository.SchoolRepository;
import org.example.school_bus_tracker_be.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, SchoolRepository schoolRepository, 
                      PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.schoolRepository = schoolRepository;
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
                User.Role.DRIVER
        );

        userRepository.save(driver);
        String token = jwtService.generateToken(driver.getEmail(), driver.getRole().name());
        return new AuthResponse(token, driver.getEmail(), driver.getRole().name());
    }

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
                User.Role.PARENT
        );

        userRepository.save(parent);
        String token = jwtService.generateToken(parent.getEmail(), parent.getRole().name());
        return new AuthResponse(token, parent.getEmail(), parent.getRole().name());
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
