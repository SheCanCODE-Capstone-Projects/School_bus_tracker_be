package org.example.school_bus_tracker_be.Service;

import org.example.school_bus_tracker_be.Dtos.auth.AuthResponse;
import org.example.school_bus_tracker_be.Dtos.auth.AuthRequest;
import org.example.school_bus_tracker_be.DTO.DriverRegisterRequest;
import org.example.school_bus_tracker_be.DTO.ParentRegisterRequest;

public interface AuthService {

    AuthResponse login(AuthRequest request);

    AuthResponse registerDriver(DriverRegisterRequest request);

    AuthResponse registerParent(ParentRegisterRequest request);
}
