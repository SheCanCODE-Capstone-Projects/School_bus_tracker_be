package org.example.school_bus_tracker_be.serviceimpl;

import java.time.LocalDateTime;

import org.example.school_bus_tracker_be.Config.JwtTokenProvider;
import org.example.school_bus_tracker_be.DTO.DriverRegisterRequest;
import org.example.school_bus_tracker_be.DTO.ParentRegisterRequest;
import org.example.school_bus_tracker_be.DTO.AdminRegisterRequest;
import org.example.school_bus_tracker_be.DTO.AdminAddDriverRequest;
import org.example.school_bus_tracker_be.DTO.AdminAddParentRequest;
import org.example.school_bus_tracker_be.DTO.AdminAddStudentRequest;
import org.example.school_bus_tracker_be.DTO.ChildInfo;
import org.example.school_bus_tracker_be.Dtos.auth.AuthRequest;
import org.example.school_bus_tracker_be.Dtos.auth.AuthResponse;
import org.example.school_bus_tracker_be.Dtos.auth.PasswordResetRequest;
import org.example.school_bus_tracker_be.Dtos.auth.PasswordResetConfirmRequest;
import org.example.school_bus_tracker_be.Model.School;
import org.example.school_bus_tracker_be.Model.User;
import org.example.school_bus_tracker_be.Model.Student;
import org.example.school_bus_tracker_be.Model.Driver;
import org.example.school_bus_tracker_be.Model.Bus;
import org.example.school_bus_tracker_be.Model.BusStop;
import org.example.school_bus_tracker_be.Model.PasswordResetToken;
import org.example.school_bus_tracker_be.Repository.SchoolRepository;
import org.example.school_bus_tracker_be.Repository.UserRepository;
import org.example.school_bus_tracker_be.Repository.StudentRepository;
import org.example.school_bus_tracker_be.Repository.DriverRepository;
import org.example.school_bus_tracker_be.Repository.BusRepository;
import org.example.school_bus_tracker_be.Repository.BusStopRepository;
import org.example.school_bus_tracker_be.Repository.PasswordResetTokenRepository;
import org.example.school_bus_tracker_be.Service.AuthService;
import org.example.school_bus_tracker_be.Service.EmailService;
import org.example.school_bus_tracker_be.Enum.Role;
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
    private final DriverRepository driverRepository;
    private final BusRepository busRepository;
    private final BusStopRepository busStopRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            SchoolRepository schoolRepository,
            StudentRepository studentRepository,
            DriverRepository driverRepository,
            BusRepository busRepository,
            BusStopRepository busStopRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider,
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailService emailService
            ) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.schoolRepository = schoolRepository;
        this.studentRepository = studentRepository;
        this.driverRepository = driverRepository;
        this.busRepository = busRepository;
        this.busStopRepository = busStopRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;

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
        return new AuthResponse(user.getId(), token, tokenProvider.getJwtExpirationMs(), user.getRole().name());
    }

    // REGISTER DRIVER
    @Override
    @Transactional
    public AuthResponse registerDriver(DriverRegisterRequest request) {
        validateUser(request.getEmail(), request.getPhone());
        
        // Validate license number is unique
        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new RuntimeException("License number already exists");
        }

        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new RuntimeException("School not found"));

        // Create User entity for authentication
        User driverUser = new User(
                school,
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhone(),
                Role.DRIVER
        );

        userRepository.save(driverUser);
        
        // Also create Driver entity with license number
        Driver driver = new Driver(
                school,
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                request.getLicenseNumber()
        );
        
        driverRepository.save(driver);

        String token = tokenProvider.generateToken(driverUser);
        return new AuthResponse(driverUser.getId(), token, tokenProvider.getJwtExpirationMs(), driverUser.getRole().name());
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
                Role.PARENT
        );

        userRepository.save(parent);

        // Create children if provided
        if (request.getChildren() != null && !request.getChildren().isEmpty()) {
            for (ChildInfo child : request.getChildren()) {
                Long busStopId = child.getBusStopId() != null ? child.getBusStopId() : request.getBusStopId();
                if (busStopId == null) {
                    throw new RuntimeException("Bus stop ID is required for child: " + child.getFullName() + ". Provide busStopId per child or once in request (busStopId).");
                }
                BusStop busStop = busStopRepository
                        .findById(busStopId)
                        .orElseThrow(() -> new RuntimeException(
                                "Bus stop not found with ID: " + busStopId +
                                " for child: " + child.getFullName() +
                                ". Please provide a valid bus stop ID."));
                if (!busStop.getSchool().getId().equals(school.getId())) {
                    throw new RuntimeException(
                            "Bus stop with ID: " + busStopId +
                            " does not belong to school ID: " + school.getId() +
                            " for child: " + child.getFullName());
                }

                Student.Gender genderEnum = null;
                if (child.getGender() != null && !child.getGender().isEmpty()) {
                    try {
                        genderEnum = Student.Gender.valueOf(child.getGender().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        // Invalid gender, leave as null
                    }
                }

                Student student = new Student(
                        school,
                        child.getFullName(),
                        child.getAge(),
                        genderEnum,
                        request.getName(), // parent name
                        request.getPhone(), // parent phone
                        request.getHomeAddress() != null ? request.getHomeAddress() : "" // address
                );
                
                // Set bus stop (it's validated above)
                student.setBusStop(busStop);

                studentRepository.save(student);
            }
        }

        String token = tokenProvider.generateToken(parent);
        return new AuthResponse(parent.getId(), token, tokenProvider.getJwtExpirationMs(), parent.getRole().name());
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
        return new AuthResponse(admin.getId(), token, tokenProvider.getJwtExpirationMs(), admin.getRole().name());
    }

    // ADMIN ADD DRIVER
    @Override
    public Driver addDriverByAdmin(AdminAddDriverRequest request, Long adminId) {
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
        
        // Only assign bus if assignedBusId is provided and is greater than 0
        if (request.getAssignedBusId() != null && request.getAssignedBusId() > 0) {
            Bus bus = busRepository.findById(request.getAssignedBusId())
                    .orElseThrow(() -> new RuntimeException("Bus not found"));
            driver.setAssignedBus(bus);
        }
        // If assignedBusId is null or 0, driver is created without assigned bus
        
        driverRepository.save(driver);

        return driver;
    }

    // ADMIN ADD PARENT (same request shape as parent registration; password optional; can include children)
    @Override
    @Transactional
    public User addParentByAdmin(AdminAddParentRequest request, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Only admins can add parents");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone number already exists");
        }

        School school = admin.getSchool();
        if (request.getSchoolId() != null && request.getSchoolId() > 0) {
            school = schoolRepository.findById(request.getSchoolId())
                    .orElseThrow(() -> new RuntimeException("School not found"));
        }

        String encodedPassword = null;
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            encodedPassword = passwordEncoder.encode(request.getPassword());
        }

        User parent = new User(
                school,
                request.getName(),
                request.getEmail(),
                encodedPassword,
                request.getPhone(),
                Role.PARENT
        );

        parent = userRepository.save(parent);

        // Create children if provided (same logic as parent self-registration)
        if (request.getChildren() != null && !request.getChildren().isEmpty()) {
            for (ChildInfo child : request.getChildren()) {
                if (child.getBusStopId() == null) {
                    throw new RuntimeException("Bus stop ID is required for child: " + child.getFullName());
                }
                BusStop busStop = busStopRepository
                        .findById(child.getBusStopId())
                        .orElseThrow(() -> new RuntimeException(
                                "Bus stop not found with ID: " + child.getBusStopId() +
                                        " for child: " + child.getFullName() + ". Please provide a valid bus stop ID."));
                if (!busStop.getSchool().getId().equals(school.getId())) {
                    throw new RuntimeException(
                            "Bus stop with ID: " + child.getBusStopId() +
                                    " does not belong to school ID: " + school.getId() +
                                    " for child: " + child.getFullName());
                }

                Student.Gender genderEnum = null;
                if (child.getGender() != null && !child.getGender().isEmpty()) {
                    try {
                        genderEnum = Student.Gender.valueOf(child.getGender().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        // ignore invalid gender
                    }
                }

                Student student = new Student(
                        school,
                        child.getFullName(),
                        child.getAge(),
                        genderEnum,
                        request.getName(),
                        request.getPhone(),
                        request.getHomeAddress() != null ? request.getHomeAddress() : ""
                );
                student.setBusStop(busStop);
                studentRepository.save(student);
            }
        }

        return parent;
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

        Student.Gender genderEnum = null;
        if (request.getGender() != null && !request.getGender().isEmpty()) {
            try {
                genderEnum = Student.Gender.valueOf(request.getGender().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid gender, leave as null
            }
        }

        Student student = new Student(
            school,
            request.getStudentName(),
            request.getAge(),
            genderEnum,
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

    // ========================= PASSWORD RESET REQUEST =========================
    @Override
    @Transactional
    public void requestPasswordReset(PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found with email: " + request.getEmail())
                );

        // Delete any existing reset tokens for this user (by userId to avoid constraint issues)
        passwordResetTokenRepository.deleteByUserId(user.getId());
        
        // Flush to ensure deletion is committed before insert
        passwordResetTokenRepository.flush();

        // Generate 6-digit verification code
        String code = generateVerificationCode();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(10); // Code expires in 10 minutes

        PasswordResetToken resetToken =
                new PasswordResetToken(code, user, expiryDate);

        passwordResetTokenRepository.save(resetToken);

        // Send email with verification code
        emailService.sendPasswordResetCode(user.getEmail(), code);
    }

    // ========================= PASSWORD RESET CONFIRM =========================
    @Override
    @Transactional
    public void confirmPasswordReset(PasswordResetConfirmRequest request) {
        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Find reset token by code
        PasswordResetToken resetToken =
                passwordResetTokenRepository.findByCode(request.getCode())
                        .orElseThrow(() ->
                                new RuntimeException("Invalid verification code")
                        );

        if (resetToken.isExpired()) {
            throw new RuntimeException("Verification code has expired. Please request a new one.");
        }

        if (resetToken.isUsed()) {
            throw new RuntimeException("Verification code has already been used. Please request a new one.");
        }

        // Update user password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    // ========================= UTIL METHODS =========================
    private void validateUser(String email, String phone) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByPhone(phone)) {
            throw new RuntimeException("Phone number already exists");
        }
    }

    /**
     * Generates a 6-digit verification code for password reset
     * @return 6-digit code as string
     */
    private String generateVerificationCode() {
        int code = (int) (Math.random() * 900000) + 100000; // Generates number between 100000 and 999999
        return String.valueOf(code);
    }

    private Integer calculateGrade(Integer age) {
        return Math.max(1, age - 5);
    }
}