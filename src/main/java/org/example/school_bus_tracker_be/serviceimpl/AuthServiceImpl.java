package org.example.school_bus_tracker_be.serviceimpl;

import org.example.school_bus_tracker_be.Config.JwtTokenProvider;
import org.example.school_bus_tracker_be.DTO.DriverRegisterRequest;
import org.example.school_bus_tracker_be.DTO.ParentRegisterRequest;
import org.example.school_bus_tracker_be.Dtos.auth.AuthRequest;
import org.example.school_bus_tracker_be.Dtos.auth.AuthResponse;
import org.example.school_bus_tracker_be.Dtos.auth.PasswordResetRequest;
import org.example.school_bus_tracker_be.Dtos.auth.PasswordResetConfirmRequest;
import org.example.school_bus_tracker_be.Model.PasswordResetToken;
import org.example.school_bus_tracker_be.Model.School;
import org.example.school_bus_tracker_be.Model.User;
import org.example.school_bus_tracker_be.Repository.PasswordResetTokenRepository;
import org.example.school_bus_tracker_be.Repository.SchoolRepository;
import org.example.school_bus_tracker_be.Repository.UserRepository;
import org.example.school_bus_tracker_be.Service.AuthService;
import org.example.school_bus_tracker_be.Service.EmailService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            SchoolRepository schoolRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider,
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailService emailService) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.schoolRepository = schoolRepository;
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

    // PASSWORD RESET REQUEST
    @Override
    @Transactional
    public void requestPasswordReset(PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getEmail()));

        // Delete any existing reset tokens for this user
        passwordResetTokenRepository.deleteByUser(user);

        // Generate new reset token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1); // Token expires in 1 hour

        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
        passwordResetTokenRepository.save(resetToken);

        // Send reset email
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    // PASSWORD RESET CONFIRM
    @Override
    @Transactional
    public void confirmPasswordReset(PasswordResetConfirmRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (resetToken.isExpired()) {
            throw new RuntimeException("Reset token has expired");
        }

        if (resetToken.isUsed()) {
            throw new RuntimeException("Reset token has already been used");
        }

        // Update user password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
}
