package org.example.school_bus_tracker_be.Service;

import org.example.school_bus_tracker_be.Dtos.auth.AuthResponse;
import org.example.school_bus_tracker_be.Dtos.auth.AuthRequest;
import org.example.school_bus_tracker_be.DTO.DriverRegisterRequest;
import org.example.school_bus_tracker_be.DTO.ParentRegisterRequest;
import org.example.school_bus_tracker_be.DTO.AdminRegisterRequest;
import org.example.school_bus_tracker_be.DTO.AdminAddDriverRequest;
import org.example.school_bus_tracker_be.DTO.AdminAddStudentRequest;
import org.example.school_bus_tracker_be.Model.User;
import org.example.school_bus_tracker_be.Model.Student;

public interface AuthService {

    AuthResponse login(AuthRequest request);

    AuthResponse registerDriver(DriverRegisterRequest request);

    AuthResponse registerParent(ParentRegisterRequest request);

    AuthResponse registerAdmin(AdminRegisterRequest request);

    User addDriverByAdmin(AdminAddDriverRequest request, Long adminId);

    Student addStudentByAdmin(AdminAddStudentRequest request, Long adminId);
}
