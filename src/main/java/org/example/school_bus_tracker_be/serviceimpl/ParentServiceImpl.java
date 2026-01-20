package org.example.school_bus_tracker_be.serviceimpl;

import org.example.school_bus_tracker_be.DTO.*;
import org.example.school_bus_tracker_be.Dtos.auth.AuthResponse;
import org.example.school_bus_tracker_be.Model.Student;
import org.example.school_bus_tracker_be.Model.User;
import org.example.school_bus_tracker_be.Repository.NotificationRepository;
import org.example.school_bus_tracker_be.Model.Notification;
import org.example.school_bus_tracker_be.Repository.StudentRepository;
import org.example.school_bus_tracker_be.Repository.UserRepository;
import org.example.school_bus_tracker_be.Service.ParentService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParentServiceImpl implements ParentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public ParentServiceImpl(StudentRepository studentRepository, UserRepository userRepository, NotificationRepository notificationRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public AuthResponse registerParentWithStudents(ParentRegisterWithStudentsRequest request) {
        // This is handled by AuthService.registerParent
        // This method can be removed or delegated to AuthService
        return new AuthResponse("mock-jwt-token", 3600000L, "PARENT");
    }

    @Override
    public List<StudentResponse> getParentStudents(Long parentId) {
        // Get parent user
        User parent = userRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        // Find students by matching parent phone and school
        // Students are linked to parents via parentPhone and parentName fields
        List<Student> students = studentRepository.findByParentPhoneAndSchoolId(
                parent.getPhone(), 
                parent.getSchool().getId()
        );

        // Convert Student entities to StudentResponse DTOs
        return students.stream()
                .map(this::convertToStudentResponse)
                .collect(Collectors.toList());
    }
    
    private StudentResponse convertToStudentResponse(Student student) {
        // Calculate level based on age (approximate)
        String level = calculateLevel(student.getAge());
        
        // Generate student number if not exists (using ID)
        String studentNumber = "ST" + String.format("%03d", student.getId());
        
        // Get gender from Student model
        String gender = student.getGender() != null ? student.getGender().name() : "UNKNOWN";
        
        return new StudentResponse(
                student.getId(),
                student.getStudentName(), // name
                studentNumber,
                student.getAge(),
                level,
                gender
        );
    }
    
    private String calculateLevel(Integer age) {
        if (age == null) return "UNKNOWN";
        // Approximate level calculation based on age
        if (age >= 18) return "S6";
        if (age >= 17) return "S5";
        if (age >= 16) return "S4";
        if (age >= 15) return "S3";
        if (age >= 14) return "S2";
        if (age >= 13) return "S1";
        if (age >= 12) return "P6";
        if (age >= 11) return "P5";
        if (age >= 10) return "P4";
        if (age >= 9) return "P3";
        if (age >= 8) return "P2";
        if (age >= 7) return "P1";
        return "NURSERY";
    }

    @Override
    public BusLocationResponse getBusLocation(Long busId) {
        if (busId == 1) {
            return new BusLocationResponse(-1.9441, 30.0632, 35, "MOVING");
        } else if (busId == 2) {
            return new BusLocationResponse(-1.9500, 30.0700, 45, "MOVING");
        } else if (busId == 3) {
            return new BusLocationResponse(-1.9300, 30.0500, 0, "STOPPED");
        } else {
            return new BusLocationResponse(0.0, 0.0, 0, "NOT_FOUND");
        }
    }

    @Override
    public List<NotificationResponse> getParentNotifications(Long parentId) {
        // DB-backed notifications
        List<Notification> notifications = notificationRepository.findByUserIdOrderByIdDesc(parentId);
        return notifications.stream()
                .map(n -> new NotificationResponse(n.getId(), n.getTitle(), n.getMessage(), n.getType().name(), n.isRead()))
                .collect(Collectors.toList());
    }
}
