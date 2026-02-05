package org.example.school_bus_tracker_be.serviceimpl;

import org.example.school_bus_tracker_be.DTO.*;
import org.example.school_bus_tracker_be.Dtos.auth.AuthResponse;
import org.example.school_bus_tracker_be.Dtos.student.StudentResponse;
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

        // Find students by matching parent phone and school (JOIN FETCH loads busStop, assignedBus)
        List<Student> students = studentRepository.findByParentPhoneAndSchoolId(
                parent.getPhone(),
                parent.getSchool().getId()
        );

        return students.stream()
                .map(this::convertToStudentResponse)
                .collect(Collectors.toList());
    }

    private StudentResponse convertToStudentResponse(Student student) {
        // Bus stop info
        StudentResponse.BusStopInfo busStopInfo = null;
        if (student.getBusStop() != null) {
            String busStopAddress = student.getBusStop().getLatitude() + ", " + student.getBusStop().getLongitude();
            busStopInfo = new StudentResponse.BusStopInfo(
                    student.getBusStop().getId(),
                    student.getBusStop().getName(),
                    busStopAddress
            );
        }

        // Assigned bus info
        StudentResponse.AssignedBusInfo busInfo = null;
        if (student.getAssignedBus() != null) {
            busInfo = new StudentResponse.AssignedBusInfo(
                    student.getAssignedBus().getId(),
                    student.getAssignedBus().getBusName(),
                    student.getAssignedBus().getBusNumber()
            );
        }

        StudentResponse response = new StudentResponse(
                student.getId(),
                student.getStudentName(),
                student.getAge(),
                student.getParentName(),
                student.getParentPhone(),
                student.getAddress(),
                busStopInfo,
                busInfo
        );
        response.setSchoolId(student.getSchool().getId());
        return response;
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
