package org.example.school_bus_tracker_be.Service;

import org.example.school_bus_tracker_be.Dtos.auth.AuthRequest;
import org.example.school_bus_tracker_be.Dtos.auth.AuthResponse;

/**
 * Service responsible for handling authentication logic.
 */
public interface AuthService {

    /**
     * Authenticate the user with the provided credentials.
     *
     * @param request the login request containing the email and password
     * @return an authentication response containing a JWT token and metadata
     */
    AuthResponse login(AuthRequest request);
}
