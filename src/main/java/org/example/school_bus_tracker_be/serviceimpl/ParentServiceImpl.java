package org.example.school_bus_tracker_be.serviceimpl;

import org.example.school_bus_tracker_be.DTO.*;
import org.example.school_bus_tracker_be.Dtos.auth.AuthResponse;
<<<<<<< Updated upstream
import org.example.school_bus_tracker_be.Service.ParentService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
=======
import org.example.school_bus_tracker_be.Exceptions.BadRequestException;
import org.example.school_bus_tracker_be.Exceptions.ResourceNotFoundException;
import org.example.school_bus_tracker_be.Model.*;
import org.example.school_bus_tracker_be.Repository.*;
import org.example.school_bus_tracker_be.Service.ParentService;
import org.example.school_bus_tracker_be.Config.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
>>>>>>> Stashed changes

@Service
public class ParentServiceImpl implements ParentService {

<<<<<<< Updated upstream
    @Override
    public AuthResponse registerParentWithStudents(ParentRegisterWithStudentsRequest request) {
        // TODO: Implement parent registration with students
        return new AuthResponse("mock-jwt-token", 3600000L, "PARENT");
=======
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final BusStopRepository busStopRepository;
    private final NotificationRepository notificationRepository;
    private final BusRepository busRepository;
    private final LocationPointRepository locationPointRepository;
    private final SchoolRepository schoolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public ParentServiceImpl(UserRepository userRepository, StudentRepository studentRepository,
                           BusStopRepository busStopRepository, NotificationRepository notificationRepository,
                           BusRepository busRepository, LocationPointRepository locationPointRepository,
                           SchoolRepository schoolRepository, PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.busStopRepository = busStopRepository;
        this.notificationRepository = notificationRepository;
        this.busRepository = busRepository;
        this.locationPointRepository = locationPointRepository;
        this.schoolRepository = schoolRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional
    public AuthResponse registerParentWithStudents(ParentRegisterWithStudentsRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Phone number already exists");
        }

        // Get default school (assuming school ID 1 exists)
        School school = schoolRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("Default school not found"));

        // Create parent user
        User parent = new User();
        parent.setSchool(school);
        parent.setName(request.getName());
        parent.setEmail(request.getEmail());
        parent.setPassword(passwordEncoder.encode(request.getPassword()));
        parent.setPhone(request.getPhone());
        parent.setRole(User.Role.PARENT);
        parent = userRepository.save(parent);

        // Get bus stop
        BusStop busStop = busStopRepository.findById(Long.parseLong(request.getBusStopId()))
                .orElseThrow(() -> new ResourceNotFoundException("Bus stop not found"));

        // Create students
        for (StudentInfo studentInfo : request.getStudents()) {
            if (studentRepository.existsByStudentNumber(studentInfo.getStudentNumber())) {
                throw new BadRequestException("Student number already exists: " + studentInfo.getStudentNumber());
            }

            Student student = new Student();
            student.setSchool(school);
            student.setParent(parent);
            student.setBusStop(busStop);
            student.setName(studentInfo.getName());
            student.setStudentNumber(studentInfo.getStudentNumber());
            student.setAge(studentInfo.getAge());
            student.setGrade(0); // Default grade
            student.setGender(studentInfo.getGender());
            student.setLevel(studentInfo.getLevel());
            studentRepository.save(student);
        }

        String token = jwtTokenProvider.generateToken(parent.getEmail(), parent.getRole().name());
        return new AuthResponse(token, parent.getRole().name());
>>>>>>> Stashed changes
    }

    @Override
    public List<StudentResponse> getParentStudents(Long parentId) {
<<<<<<< Updated upstream
        List<StudentResponse> students = new ArrayList<>();
        
        if (parentId == 1) {
            students.add(new StudentResponse(1L, "Aurore", "ST101", 12, "P6", "FEMALE"));
        } else if (parentId == 2) {
            students.add(new StudentResponse(2L, "Emma Smith", "ST102", 10, "P4", "FEMALE"));
            students.add(new StudentResponse(3L, "James Smith", "ST103", 8, "P2", "MALE"));
        } else if (parentId == 3) {
            students.add(new StudentResponse(4L, "Carlos Garcia", "ST104", 14, "S1", "MALE"));
        } else {
            // Return empty list for unknown parent IDs
        }
        
        return students;
=======
        List<Student> students = studentRepository.findByParentId(parentId);
        return students.stream()
                .map(student -> new StudentResponse(
                        student.getId(),
                        student.getName(),
                        student.getStudentNumber(),
                        student.getAge(),
                        student.getLevel(),
                        student.getGender()
                ))
                .collect(Collectors.toList());
>>>>>>> Stashed changes
    }

    @Override
    public BusLocationResponse getBusLocation(Long busId) {
<<<<<<< Updated upstream
        if (busId == 1) {
            return new BusLocationResponse(-1.9441, 30.0632, 35, "MOVING");
        } else if (busId == 2) {
            return new BusLocationResponse(-1.9500, 30.0700, 45, "MOVING");
        } else if (busId == 3) {
            return new BusLocationResponse(-1.9300, 30.0500, 0, "STOPPED");
        } else {
            return new BusLocationResponse(0.0, 0.0, 0, "NOT_FOUND");
        }
=======
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));

        LocationPoint latestLocation = locationPointRepository.findTopByBusIdOrderByTimestampDesc(busId)
                .orElseThrow(() -> new ResourceNotFoundException("No location data found for bus"));

        String status = bus.getStatus() == Bus.Status.ACTIVE ? "MOVING" : "STOPPED";
        
        return new BusLocationResponse(
                latestLocation.getLatitude(),
                latestLocation.getLongitude(),
                latestLocation.getSpeed(),
                status
        );
>>>>>>> Stashed changes
    }

    @Override
    public List<NotificationResponse> getParentNotifications(Long parentId) {
<<<<<<< Updated upstream
        List<NotificationResponse> notifications = new ArrayList<>();
        
        if (parentId == 1) {
            notifications.add(new NotificationResponse(1L, "Bus Update", "Your child's bus is on the way", "INFO", false));
            notifications.add(new NotificationResponse(2L, "Arrival Alert", "Bus arriving in 5 minutes", "WARNING", false));
        } else if (parentId == 2) {
            notifications.add(new NotificationResponse(3L, "Departure Notice", "Bus has left school", "INFO", true));
            notifications.add(new NotificationResponse(4L, "Emergency Alert", "Traffic delay expected", "EMERGENCY", false));
        } else if (parentId == 3) {
            notifications.add(new NotificationResponse(5L, "Schedule Change", "Bus route updated", "WARNING", false));
        }
        
        return notifications;
=======
        List<Notification> notifications = notificationRepository.findByUserIdOrderByIdDesc(parentId);
        return notifications.stream()
                .map(notification -> new NotificationResponse(
                        notification.getId(),
                        notification.getTitle(),
                        notification.getMessage(),
                        notification.getType().name(),
                        notification.isRead()
                ))
                .collect(Collectors.toList());
>>>>>>> Stashed changes
    }
}
