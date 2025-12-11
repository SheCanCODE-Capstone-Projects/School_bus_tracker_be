package org.example.school_bus_tracker_be.Service;

<<<<<<< HEAD
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
=======
import org.example.school_bus_tracker_be.Dtos.auth.AuthResponse;
import org.example.school_bus_tracker_be.Dtos.auth.AuthRequest;
import org.example.school_bus_tracker_be.DTO.DriverRegisterRequest;
import org.example.school_bus_tracker_be.DTO.ParentRegisterRequest;
>>>>>>> 96e02c67735a58d6485450f83e3d96eba5a6aeb5

public interface AuthService {

    AuthResponse login(AuthRequest request);

    AuthResponse registerDriver(DriverRegisterRequest request);

    AuthResponse registerParent(ParentRegisterRequest request);
}
