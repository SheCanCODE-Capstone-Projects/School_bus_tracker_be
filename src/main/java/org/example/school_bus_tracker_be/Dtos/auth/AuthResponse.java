package org.example.school_bus_tracker_be.Dtos.auth;

/**
 * DTO representing the response returned from a successful authentication
 * request.
 *
 * <p>
 * Contains the generated JWT access token, its type (usually "Bearer"), the
 * expiration time in milliseconds, and the role of the authenticated user.
 */
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private String role;

    public AuthResponse() {}

    public AuthResponse(String accessToken, long expiresIn, String role) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.role = role;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
